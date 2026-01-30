package com.supermercado.service;

import com.supermercado.model.Usuario;
import org.springframework.stereotype.Service;

@Service
public class SessaoService {

    private Usuario usuarioLogado;

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public boolean temPermissao(Usuario.TipoPerfil... perfisPermitidos) {
        if (usuarioLogado == null)
            return false;

        // Admin sempre tem permissão
        if (usuarioLogado.isAdmin())
            return true;

        for (Usuario.TipoPerfil perfil : perfisPermitidos) {
            if (usuarioLogado.getPerfil() == perfil) {
                return true;
            }
        }
        return false;
    }

    public void logout() {
        this.usuarioLogado = null;
    }
}
