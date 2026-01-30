package com.supermercado.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDateTime;

/**
 * Entidade que representa um usuário do sistema
 */
@Entity
@Table(name = "usuarios", indexes = {
        @Index(name = "idx_username", columnList = "username", unique = true)
})
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String senha;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoPerfil perfil = TipoPerfil.OPERADOR;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "ultimo_acesso")
    private LocalDateTime ultimoAcesso;

    public Usuario() {
    }

    public Usuario(Long id, String username, String senha, String nome, String email, TipoPerfil perfil, Boolean ativo,
            LocalDateTime dataCriacao, LocalDateTime ultimoAcesso) {
        this.id = id;
        this.username = username;
        this.senha = senha;
        this.nome = nome;
        this.email = email;
        this.perfil = perfil;
        this.ativo = ativo;
        this.dataCriacao = dataCriacao;
        this.ultimoAcesso = ultimoAcesso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public TipoPerfil getPerfil() {
        return perfil;
    }

    public void setPerfil(TipoPerfil perfil) {
        this.perfil = perfil;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getUltimoAcesso() {
        return ultimoAcesso;
    }

    public void setUltimoAcesso(LocalDateTime ultimoAcesso) {
        this.ultimoAcesso = ultimoAcesso;
    }

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }

    /**
     * Define a senha com hash BCrypt
     */
    public void setPassword(String senhaPlain) {
        this.senha = BCrypt.hashpw(senhaPlain, BCrypt.gensalt(12));
    }

    /**
     * Verifica se a senha está correta
     */
    public boolean checkPassword(String senhaPlain) {
        return BCrypt.checkpw(senhaPlain, this.senha);
    }

    /**
     * Registra último acesso
     */
    public void registrarAcesso() {
        this.ultimoAcesso = LocalDateTime.now();
    }

    /**
     * Verifica se é administrador
     */
    @Transient
    public boolean isAdmin() {
        return perfil == TipoPerfil.ADMINISTRADOR;
    }

    /**
     * Tipos de perfil de usuário
     */
    public enum TipoPerfil {
        ADMINISTRADOR("Administrador", "Acesso total ao sistema"),
        OPERADOR("Operador de Caixa", "Acesso ao PDV e vendas"),
        GERENTE("Gerente", "Acesso a vendas e relatórios");

        private final String nome;
        private final String descricao;

        TipoPerfil(String nome, String descricao) {
            this.nome = nome;
            this.descricao = descricao;
        }

        public String getNome() {
            return nome;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    @Override
    public String toString() {
        return nome + " (" + perfil.getNome() + ")";
    }
}
