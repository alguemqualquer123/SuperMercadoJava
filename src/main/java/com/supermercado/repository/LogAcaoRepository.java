package com.supermercado.repository;

import com.supermercado.model.LogAcao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LogAcaoRepository extends JpaRepository<LogAcao, Long> {

    List<LogAcao> findByDataHoraBetweenOrderByDataHoraDesc(LocalDateTime inicio, LocalDateTime fim);

    List<LogAcao> findByUsuarioIdOrderByDataHoraDesc(Long usuarioId);
}
