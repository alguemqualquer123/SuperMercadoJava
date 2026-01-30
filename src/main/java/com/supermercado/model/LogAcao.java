package com.supermercado.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade para registro de ações dos usuários (auditoria)
 */
@Entity
@Table(name = "log_acoes", indexes = {
        @Index(name = "idx_data_hora", columnList = "dataHora"),
        @Index(name = "idx_usuario", columnList = "usuario_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogAcao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoAcao acao;

    @Column(nullable = false, length = 100)
    private String entidade;

    @Column(name = "entidade_id")
    private Long entidadeId;

    @Column(length = 1000)
    private String descricao;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @PrePersist
    protected void onCreate() {
        if (dataHora == null) {
            dataHora = LocalDateTime.now();
        }
    }

    /**
     * Construtor para criar log rapidamente
     */
    public LogAcao(Usuario usuario, TipoAcao acao, String entidade, Long entidadeId, String descricao) {
        this.usuario = usuario;
        this.acao = acao;
        this.entidade = entidade;
        this.entidadeId = entidadeId;
        this.descricao = descricao;
        this.dataHora = LocalDateTime.now();
    }

    /**
     * Tipos de ação
     */
    public enum TipoAcao {
        LOGIN,
        LOGOUT,
        CREATE,
        UPDATE,
        DELETE,
        VENDA_FINALIZADA,
        VENDA_CANCELADA,
        ESTOQUE_ATUALIZADO,
        BACKUP_REALIZADO,
        ERRO
    }
}
