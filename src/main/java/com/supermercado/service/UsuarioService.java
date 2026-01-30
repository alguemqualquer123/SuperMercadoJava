package com.supermercado.service;

import com.supermercado.model.LogAcao;
import com.supermercado.model.Usuario;
import com.supermercado.repository.LogAcaoRepository;
import com.supermercado.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service para autenticação e gerenciamento de usuários
 */
@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final LogAcaoRepository logAcaoRepository;

    private static Usuario usuarioLogado;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, LogAcaoRepository logAcaoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.logAcaoRepository = logAcaoRepository;
    }

    /**
     * Realiza login no sistema
     */
    @Transactional
    public Usuario login(String username, String senha) {
        logger.debug("Tentativa de login: {}", username);

        Optional<Usuario> optUsuario = usuarioRepository.findByUsername(username);

        if (optUsuario.isEmpty()) {
            logger.warn("Usuário não encontrado: {}", username);
            throw new IllegalArgumentException("Usuário ou senha inválidos");
        }

        Usuario usuario = optUsuario.get();

        if (!usuario.getAtivo()) {
            logger.warn("Tentativa de login com usuário inativo: {}", username);
            throw new IllegalStateException("Usuário inativo");
        }

        if (!usuario.checkPassword(senha)) {
            logger.warn("Senha inválida para usuário: {}", username);
            throw new IllegalArgumentException("Usuário ou senha inválidos");
        }

        // Registra último acesso
        usuario.registrarAcesso();
        usuarioRepository.save(usuario);

        // Registra log de login
        registrarLog(usuario, LogAcao.TipoAcao.LOGIN, "Usuario", usuario.getId(),
                "Login realizado com sucesso");

        usuarioLogado = usuario;
        logger.info("Login realizado com sucesso: {}", username);

        return usuario;
    }

    /**
     * Realiza logout
     */
    @Transactional
    public void logout() {
        if (usuarioLogado != null) {
            logger.info("Logout: {}", usuarioLogado.getUsername());
            registrarLog(usuarioLogado, LogAcao.TipoAcao.LOGOUT, "Usuario",
                    usuarioLogado.getId(), "Logout realizado");
            usuarioLogado = null;
        }
    }

    /**
     * Retorna o usuário logado
     */
    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    /**
     * Verifica se há usuário logado
     */
    public boolean isLogado() {
        return usuarioLogado != null;
    }

    /**
     * Cria um novo usuário
     */
    @Transactional
    public Usuario criarUsuario(String username, String senha, String nome,
            String email, Usuario.TipoPerfil perfil) {
        logger.debug("Criando novo usuário: {}", username);

        if (usuarioRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username já existe: " + username);
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPassword(senha); // Vai fazer hash automaticamente
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setPerfil(perfil);
        usuario.setAtivo(true);

        Usuario saved = usuarioRepository.save(usuario);

        if (usuarioLogado != null) {
            registrarLog(usuarioLogado, LogAcao.TipoAcao.CREATE, "Usuario",
                    saved.getId(), "Usuário criado: " + username);
        }

        logger.info("Usuário criado com sucesso: {}", username);
        return saved;
    }

    /**
     * Lista usuários ativos
     */
    public List<Usuario> listarAtivos() {
        return usuarioRepository.findAtivos();
    }

    /**
     * Registra log de ação
     */
    @Transactional
    public void registrarLog(Usuario usuario, LogAcao.TipoAcao acao, String entidade,
            Long entidadeId, String descricao) {
        try {
            LogAcao log = new LogAcao(usuario, acao, entidade, entidadeId, descricao);
            logAcaoRepository.save(log);
        } catch (Exception e) {
            logger.error("Erro ao registrar log", e);
        }
    }

    /**
     * Cria usuário administrativo padrão se não existir
     */
    @Transactional
    public void criarUsuarioAdminSeNaoExistir() {
        if (!usuarioRepository.existsByUsername("admin")) {
            logger.info("Criando usuário administrador padrão");
            criarUsuario("admin", "admin123", "Administrador",
                    "admin@supermercado.com", Usuario.TipoPerfil.ADMINISTRADOR);
        }
    }
}
