package com.supermercado.service;

import com.supermercado.model.Compra;
import com.supermercado.model.ItemCompra;
import com.supermercado.model.Produto;
import com.supermercado.model.Fornecedor;
import com.supermercado.model.Usuario;
import com.supermercado.model.LogAcao;
import com.supermercado.repository.CompraRepository;
import com.supermercado.repository.FornecedorRepository;
import com.supermercado.repository.ProdutoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class CompraService {

    private static final Logger logger = LoggerFactory.getLogger(CompraService.class);

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private FornecedorRepository fornecedorRepository;

    @Autowired(required = false)
    private LogService logService;

    public List<Compra> listarTodas() {
        return compraRepository.findAll();
    }

    public List<Fornecedor> listarFornecedores() {
        return fornecedorRepository.findAll();
    }

    @Transactional
    public Compra salvarCompra(Compra compra) {
        logger.debug("Salvando compra: Fornecedor {}", compra.getFornecedor().getNome());

        if (compra.getFornecedor() == null) {
            throw new IllegalArgumentException("Fornecedor é obrigatório");
        }

        if (compra.getUsuario() == null) {
            throw new IllegalArgumentException("Usuário é obrigatório");
        }

        if (compra.getItens() == null || compra.getItens().isEmpty()) {
            throw new IllegalArgumentException("Compra deve ter pelo menos um item");
        }

        // Atualiza o estoque dos produtos
        for (ItemCompra item : compra.getItens()) {
            Produto produto = item.getProduto();
            produto.adicionarEstoque(item.getQuantidade());
            // Opcional: Atualizar preço de custo do produto com base na última compra
            produto.setPrecoCusto(item.getPrecoUnitario());
            produtoRepository.save(produto);
        }

        compra.atualizarTotal();
        Compra saved = compraRepository.save(compra);
        logger.info("Compra salva com sucesso: ID {}", saved.getId());
        
        // Registra log
        Usuario usuario = saved.getUsuario();
        if (usuario != null && logService != null) {
            String descricao = String.format("Entrada de mercadoria registrada - Fornecedor: %s | Total: R$ %.2f | Itens: %d | Nota Fiscal: %s",
                    saved.getFornecedor().getNome(), saved.getValorTotal(), saved.getItens().size(), 
                    saved.getNumeroNotaFiscal() != null ? saved.getNumeroNotaFiscal() : "N/A");
            logService.registrarLog(usuario, LogAcao.TipoAcao.CREATE, "Compra", saved.getId(), descricao);
        }
        
        return saved;
    }

    public Optional<Compra> buscarPorId(Long id) {
        return compraRepository.findById(id);
    }
}
