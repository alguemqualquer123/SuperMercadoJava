package com.supermercado.repository;

import com.supermercado.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    Optional<Produto> findByCodigoBarras(String codigoBarras);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true ORDER BY p.nome")
    List<Produto> findAtivos();

    @Query("SELECT p FROM Produto p WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND p.ativo = true ORDER BY p.nome")
    List<Produto> findByNomeContainingIgnoreCase(@Param("nome") String nome);

    @Query("SELECT p FROM Produto p WHERE p.categoria.id = :categoriaId AND p.ativo = true ORDER BY p.nome")
    List<Produto> findByCategoriaId(@Param("categoriaId") Long categoriaId);

    @Query("SELECT p FROM Produto p WHERE p.quantidadeEstoque <= p.estoqueMinimo AND p.ativo = true ORDER BY p.quantidadeEstoque")
    List<Produto> findComEstoqueBaixo();

    boolean existsByCodigoBarras(String codigoBarras);
}
