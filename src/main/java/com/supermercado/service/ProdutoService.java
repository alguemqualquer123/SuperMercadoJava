package com.supermercado.service;

import com.supermercado.model.LogAcao;
import com.supermercado.model.Produto;
import com.supermercado.model.Usuario;
import com.supermercado.repository.ProdutoRepository;
import com.supermercado.util.Validador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service para lógica de negócio de Produto
 */
@Service
public class ProdutoService {

    private static final Logger logger = LoggerFactory.getLogger(ProdutoService.class);
    private final ProdutoRepository produtoRepository;
    private final LogService logService;
    private final UsuarioService usuarioService;

    @Autowired
    public ProdutoService(ProdutoRepository produtoRepository, LogService logService, UsuarioService usuarioService) {
        this.produtoRepository = produtoRepository;
        this.logService = logService;
        this.usuarioService = usuarioService;
    }

    /**
     * Salva um produto com validações
     */
    @Transactional
    public Produto salvar(Produto produto) {
        logger.debug("Salvando produto: {}", produto.getNome());

        // Validações
        Validador.validarProduto(produto);

        // Verifica se código de barras já existe (para novos produtos)
        if (produto.getId() == null && produtoRepository.existsByCodigoBarras(produto.getCodigoBarras())) {
            throw new IllegalArgumentException("Código de barras já cadastrado!");
        }

        try {
            Produto saved = produtoRepository.save(produto);
            logger.info("Produto salvo com sucesso: ID {}", saved.getId());
            
            // Registra log
            Usuario usuario = usuarioService.getUsuarioLogado();
            if (usuario != null) {
                String acao = produto.getId() == null ? "CREATE" : "UPDATE";
                String descricao = String.format("Produto %s: %s (Código: %s, Preço: R$ %.2f)", 
                    produto.getId() == null ? "cadastrado" : "atualizado",
                    saved.getNome(), saved.getCodigoBarras(), saved.getPrecoVenda());
                logService.registrarLog(usuario, 
                    produto.getId() == null ? LogAcao.TipoAcao.CREATE : LogAcao.TipoAcao.UPDATE,
                    "Produto", saved.getId(), descricao);
            }
            
            return saved;
        } catch (Exception e) {
            logger.error("Erro ao salvar produto", e);
            throw new RuntimeException("Erro ao salvar produto: " + e.getMessage(), e);
        }
    }

    /**
     * Atualiza um produto
     */
    @Transactional
    public Produto atualizar(Produto produto) {
        logger.debug("Atualizando produto: ID {}", produto.getId());

        Validador.validarProduto(produto);

        if (produto.getId() == null) {
            throw new IllegalArgumentException("ID do produto não pode ser nulo para atualização");
        }

        try {
            Produto updated = produtoRepository.save(produto);
            logger.info("Produto atualizado com sucesso: ID {}", updated.getId());
            
            // Registra log
            Usuario usuario = usuarioService.getUsuarioLogado();
            if (usuario != null) {
                String descricao = String.format("Produto atualizado: %s (ID: %d)", updated.getNome(), updated.getId());
                logService.registrarLog(usuario, LogAcao.TipoAcao.UPDATE, "Produto", updated.getId(), descricao);
            }
            
            return updated;
        } catch (Exception e) {
            logger.error("Erro ao atualizar produto", e);
            throw new RuntimeException("Erro ao atualizar produto: " + e.getMessage(), e);
        }
    }

    /**
     * Desativa um produto (soft delete)
     */
    @Transactional
    public void desativar(Long id) {
        logger.debug("Desativando produto: ID {}", id);

        Optional<Produto> produto = produtoRepository.findById(id);
        if (produto.isPresent()) {
            Produto p = produto.get();
            p.setAtivo(false);
            produtoRepository.save(p);
            logger.info("Produto desativado: ID {}", id);
            
            // Registra log
            Usuario usuario = usuarioService.getUsuarioLogado();
            if (usuario != null) {
                String descricao = String.format("Produto desativado: %s (ID: %d)", p.getNome(), p.getId());
                logService.registrarLog(usuario, LogAcao.TipoAcao.DELETE, "Produto", p.getId(), descricao);
            }
        } else {
            throw new IllegalArgumentException("Produto não encontrado: ID " + id);
        }
    }

    /**
     * Busca produto por ID
     */
    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    /**
     * Busca produto por código de barras
     */
    public Optional<Produto> buscarPorCodigoBarras(String codigoBarras) {
        return produtoRepository.findByCodigoBarras(codigoBarras);
    }

    /**
     * Busca produtos ativos
     */
    public List<Produto> listarAtivos() {
        return produtoRepository.findAtivos();
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    /**
     * Busca produtos por nome
     */
    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }

    /**
     * Busca produtos com estoque baixo
     */
    public List<Produto> listarEstoqueBaixo() {
        return produtoRepository.findComEstoqueBaixo();
    }

    /**
     * Atualiza estoque do produto
     */
    @Transactional
    public void atualizarEstoque(Long produtoId, int quantidade) {
        logger.debug("Atualizando estoque do produto ID {}: quantidade {}", produtoId, quantidade);

        Optional<Produto> optProduto = produtoRepository.findById(produtoId);
        if (optProduto.isEmpty()) {
            throw new IllegalArgumentException("Produto não encontrado: ID " + produtoId);
        }

        Produto produto = optProduto.get();

        if (quantidade > 0) {
            produto.adicionarEstoque(quantidade);
        } else {
            produto.removerEstoque(Math.abs(quantidade));
        }

        produtoRepository.save(produto);
        logger.info("Estoque atualizado para produto ID {}: nova quantidade {}",
                produtoId, produto.getQuantidadeEstoque());
    }
}
