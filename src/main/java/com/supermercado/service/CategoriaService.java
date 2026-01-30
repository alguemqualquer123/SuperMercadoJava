package com.supermercado.service;

import com.supermercado.model.Categoria;
import com.supermercado.repository.CategoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service para lógica de negócio de Categoria
 */
@Service
public class CategoriaService {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaService.class);
    private final CategoriaRepository categoriaRepository;

    @Autowired
    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    /**
     * Salva uma categoria
     */
    @Transactional
    public Categoria salvar(Categoria categoria) {
        logger.debug("Salvando categoria: {}", categoria.getNome());

        if (categoria.getNome() == null || categoria.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da categoria é obrigatório");
        }

        try {
            Categoria saved = categoriaRepository.save(categoria);
            logger.info("Categoria salva com sucesso: ID {}", saved.getId());
            return saved;
        } catch (Exception e) {
            logger.error("Erro ao salvar categoria", e);
            throw new RuntimeException("Erro ao salvar categoria: " + e.getMessage(), e);
        }
    }

    /**
     * Atualiza uma categoria
     */
    @Transactional
    public Categoria atualizar(Categoria categoria) {
        logger.debug("Atualizando categoria: ID {}", categoria.getId());

        if (categoria.getId() == null) {
            throw new IllegalArgumentException("ID da categoria não pode ser nulo para atualização");
        }

        try {
            Categoria updated = categoriaRepository.save(categoria);
            logger.info("Categoria atualizada com sucesso: ID {}", updated.getId());
            return updated;
        } catch (Exception e) {
            logger.error("Erro ao atualizar categoria", e);
            throw new RuntimeException("Erro ao atualizar categoria: " + e.getMessage(), e);
        }
    }

    /**
     * Desativa uma categoria (soft delete)
     */
    @Transactional
    public void desativar(Long id) {
        logger.debug("Desativando categoria: ID {}", id);

        Optional<Categoria> categoria = categoriaRepository.findById(id);
        if (categoria.isPresent()) {
            categoria.get().setAtivo(false);
            categoriaRepository.save(categoria.get());
            logger.info("Categoria desativada: ID {}", id);
        } else {
            throw new IllegalArgumentException("Categoria não encontrada: ID " + id);
        }
    }

    /**
     * Busca categoria por ID
     */
    public Optional<Categoria> buscarPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    /**
     * Lista categorias ativas
     */
    public List<Categoria> listarAtivas() {
        return categoriaRepository.findAtivas();
    }

    /**
     * Lista todas as categorias
     */
    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    /**
     * Busca categorias por nome
     */
    public List<Categoria> buscarPorNome(String nome) {
        return categoriaRepository.findByNomeContainingIgnoreCase(nome);
    }
}
