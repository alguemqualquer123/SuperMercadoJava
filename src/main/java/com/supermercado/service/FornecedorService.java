package com.supermercado.service;

import com.supermercado.model.Fornecedor;
import com.supermercado.model.LogAcao;
import com.supermercado.model.Usuario;
import com.supermercado.repository.FornecedorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service para lógica de negócio de Fornecedor
 */
@Service
public class FornecedorService {

    private static final Logger logger = LoggerFactory.getLogger(FornecedorService.class);
    private final FornecedorRepository fornecedorRepository;
    private final LogService logService;
    private final UsuarioService usuarioService;

    @Autowired
    public FornecedorService(FornecedorRepository fornecedorRepository, LogService logService, UsuarioService usuarioService) {
        this.fornecedorRepository = fornecedorRepository;
        this.logService = logService;
        this.usuarioService = usuarioService;
    }

    /**
     * Salva um fornecedor
     */
    @Transactional
    public Fornecedor salvar(Fornecedor fornecedor) {
        logger.debug("Salvando fornecedor: {}", fornecedor.getNome());

        if (fornecedor.getNome() == null || fornecedor.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do fornecedor é obrigatório");
        }

        if (fornecedor.getCnpj() == null || fornecedor.getCnpj().trim().isEmpty()) {
            throw new IllegalArgumentException("CNPJ é obrigatório");
        }

        // Verifica se CNPJ já existe (para novos fornecedores)
        if (fornecedor.getId() == null) {
            Optional<Fornecedor> existente = fornecedorRepository.findByCnpj(fornecedor.getCnpj());
            if (existente.isPresent()) {
                throw new IllegalArgumentException("CNPJ já cadastrado!");
            }
        }

        try {
            Fornecedor saved = fornecedorRepository.save(fornecedor);
            logger.info("Fornecedor salvo com sucesso: ID {}", saved.getId());
            
            // Registra log
            Usuario usuario = usuarioService.getUsuarioLogado();
            if (usuario != null) {
                String descricao = String.format("Fornecedor %s: %s (CNPJ: %s)", 
                    fornecedor.getId() == null ? "cadastrado" : "atualizado", saved.getNome(), saved.getCnpj());
                logService.registrarLog(usuario, 
                    fornecedor.getId() == null ? LogAcao.TipoAcao.CREATE : LogAcao.TipoAcao.UPDATE,
                    "Fornecedor", saved.getId(), descricao);
            }
            
            return saved;
        } catch (Exception e) {
            logger.error("Erro ao salvar fornecedor", e);
            throw new RuntimeException("Erro ao salvar fornecedor: " + e.getMessage(), e);
        }
    }

    /**
     * Atualiza um fornecedor
     */
    @Transactional
    public Fornecedor atualizar(Fornecedor fornecedor) {
        logger.debug("Atualizando fornecedor: ID {}", fornecedor.getId());

        if (fornecedor.getId() == null) {
            throw new IllegalArgumentException("ID do fornecedor não pode ser nulo para atualização");
        }

        try {
            Fornecedor updated = fornecedorRepository.save(fornecedor);
            logger.info("Fornecedor atualizado com sucesso: ID {}", updated.getId());
            return updated;
        } catch (Exception e) {
            logger.error("Erro ao atualizar fornecedor", e);
            throw new RuntimeException("Erro ao atualizar fornecedor: " + e.getMessage(), e);
        }
    }

    /**
     * Desativa um fornecedor (soft delete)
     */
    @Transactional
    public void desativar(Long id) {
        logger.debug("Desativando fornecedor: ID {}", id);

        Optional<Fornecedor> fornecedor = fornecedorRepository.findById(id);
        if (fornecedor.isPresent()) {
            Fornecedor f = fornecedor.get();
            f.setAtivo(false);
            fornecedorRepository.save(f);
            logger.info("Fornecedor desativado: ID {}", id);
            
            // Registra log
            Usuario usuario = usuarioService.getUsuarioLogado();
            if (usuario != null) {
                String descricao = String.format("Fornecedor desativado: %s (ID: %d)", f.getNome(), f.getId());
                logService.registrarLog(usuario, LogAcao.TipoAcao.DELETE, "Fornecedor", f.getId(), descricao);
            }
        } else {
            throw new IllegalArgumentException("Fornecedor não encontrado: ID " + id);
        }
    }

    /**
     * Busca fornecedor por ID
     */
    public Optional<Fornecedor> buscarPorId(Long id) {
        return fornecedorRepository.findById(id);
    }

    /**
     * Lista fornecedores ativos
     */
    public List<Fornecedor> listarAtivos() {
        return fornecedorRepository.findAll().stream()
                .filter(Fornecedor::getAtivo)
                .toList();
    }

    /**
     * Lista todos os fornecedores
     */
    public List<Fornecedor> listarTodos() {
        return fornecedorRepository.findAll();
    }

    /**
     * Busca fornecedor por CNPJ
     */
    public Optional<Fornecedor> buscarPorCnpj(String cnpj) {
        return fornecedorRepository.findByCnpj(cnpj);
    }
}

