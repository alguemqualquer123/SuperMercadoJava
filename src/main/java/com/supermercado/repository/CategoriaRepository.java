package com.supermercado.repository;

import com.supermercado.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    @Query("SELECT c FROM Categoria c WHERE c.ativo = true ORDER BY c.nome")
    List<Categoria> findAtivas();

    @Query("SELECT c FROM Categoria c WHERE LOWER(c.nome) LIKE LOWER(CONCAT('%', :nome, '%')) ORDER BY c.nome")
    List<Categoria> findByNomeContainingIgnoreCase(@Param("nome") String nome);

    Optional<Categoria> findByNome(String nome);
}
