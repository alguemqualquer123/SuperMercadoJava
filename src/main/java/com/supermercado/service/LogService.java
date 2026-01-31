package com.supermercado.service;

import com.supermercado.model.LogAcao;
import com.supermercado.model.Usuario;
import com.supermercado.repository.LogAcaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service para gerenciamento de logs de auditoria
 */
@Service
public class LogService {

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);
    private final LogAcaoRepository logAcaoRepository;

    @Autowired
    public LogService(LogAcaoRepository logAcaoRepository) {
        this.logAcaoRepository = logAcaoRepository;
    }

    /**
     * Registra uma ação no log de auditoria
     */
    @Transactional
    public void registrarLog(Usuario usuario, LogAcao.TipoAcao acao, String entidade,
            Long entidadeId, String descricao) {
        try {
            LogAcao log = new LogAcao(usuario, acao, entidade, entidadeId, descricao);
            logAcaoRepository.save(log);
            logger.debug("Log registrado: {} - {} - {}", usuario != null ? usuario.getUsername() : "Sistema", acao, descricao);
        } catch (Exception e) {
            logger.error("Erro ao registrar log", e);
        }
    }

    /**
     * Registra uma ação com descrição detalhada
     */
    @Transactional
    public void registrarLogDetalhado(Usuario usuario, LogAcao.TipoAcao acao, String entidade,
            Long entidadeId, String descricao, String detalhes) {
        try {
            String descricaoCompleta = descricao;
            if (detalhes != null && !detalhes.isEmpty()) {
                descricaoCompleta += " | Detalhes: " + detalhes;
            }
            registrarLog(usuario, acao, entidade, entidadeId, descricaoCompleta);
        } catch (Exception e) {
            logger.error("Erro ao registrar log detalhado", e);
        }
    }

    /**
     * Lista todos os logs
     */
    public List<LogAcao> listarTodos() {
        return logAcaoRepository.findAllByOrderByDataHoraDesc();
    }

    /**
     * Lista logs de um usuário específico
     */
    public List<LogAcao> listarPorUsuario(Long usuarioId) {
        return logAcaoRepository.findByUsuarioIdOrderByDataHoraDesc(usuarioId);
    }

    /**
     * Lista logs de uma entidade específica
     */
    public List<LogAcao> listarPorEntidade(String entidade, Long entidadeId) {
        return logAcaoRepository.findByEntidadeAndEntidadeIdOrderByDataHoraDesc(entidade, entidadeId);
    }

    /**
     * Lista logs por tipo de ação
     */
    public List<LogAcao> listarPorAcao(LogAcao.TipoAcao acao) {
        return logAcaoRepository.findByAcaoOrderByDataHoraDesc(acao);
    }

    /**
     * Lista logs em um período
     */
    public List<LogAcao> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return logAcaoRepository.findByDataHoraBetweenOrderByDataHoraDesc(inicio, fim);
    }

    /**
     * Lista logs recentes (últimas N horas)
     */
    public List<LogAcao> listarRecentes(int horas) {
        LocalDateTime inicio = LocalDateTime.now().minusHours(horas);
        return listarPorPeriodo(inicio, LocalDateTime.now());
    }
}

