package com.supermercado.repository;

import com.supermercado.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    @Query("SELECT u FROM Usuario u WHERE u.ativo = true ORDER BY u.nome")
    List<Usuario> findAtivos();

    boolean existsByUsername(String username);
}
