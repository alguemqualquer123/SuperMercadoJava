package com.supermercado.util;

import com.supermercado.model.Produto;
import com.supermercado.model.Venda;

import java.math.BigDecimal;

/**
 * Classe utilitária para validações centralizadas
 */
public class Validador {

    /**
     * Valida um produto
     */
    public static void validarProduto(Produto produto) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo");
        }

        if (produto.getCodigoBarras() == null || produto.getCodigoBarras().trim().isEmpty()) {
            throw new IllegalArgumentException("Código de barras é obrigatório");
        }

        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do produto é obrigatório");
        }

        if (produto.getCategoria() == null) {
            throw new IllegalArgumentException("Categoria é obrigatória");
        }

        if (produto.getPrecoCusto() == null || produto.getPrecoCusto().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço de custo inválido");
        }

        if (produto.getPrecoVenda() == null || produto.getPrecoVenda().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço de venda inválido");
        }

        if (produto.getPrecoVenda().compareTo(produto.getPrecoCusto()) < 0) {
            throw new IllegalArgumentException("Preço de venda não pode ser menor que o preço de custo");
        }

        if (produto.getQuantidadeEstoque() == null || produto.getQuantidadeEstoque() < 0) {
            throw new IllegalArgumentException("Quantidade em estoque inválida");
        }

        if (produto.getEstoqueMinimo() == null || produto.getEstoqueMinimo() < 0) {
            throw new IllegalArgumentException("Estoque mínimo inválido");
        }
    }

    /**
     * Valida uma venda
     */
    public static void validarVenda(Venda venda) {
        if (venda == null) {
            throw new IllegalArgumentException("Venda não pode ser nula");
        }

        if (venda.getUsuario() == null) {
            throw new IllegalArgumentException("Usuário é obrigatório");
        }

        if (venda.getItens() == null || venda.getItens().isEmpty()) {
            throw new IllegalArgumentException("Venda deve ter pelo menos um item");
        }

        if (venda.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Total da venda inválido");
        }
    }

    /**
     * Valida código de barras (formato básico EAN-13)
     */
    public static boolean isCodigoBarrasValido(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return false;
        }

        // Remove espaços
        codigo = codigo.trim();

        // Aceita códigos de 8 a 13 dígitos
        if (codigo.length() < 8 || codigo.length() > 13) {
            return false;
        }

        // Verifica se são apenas números
        return codigo.matches("\\d+");
    }

    /**
     * Valida email
     */
    public static boolean isEmailValido(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(regex);
    }

    /**
     * Valida username
     */
    public static boolean isUsernameValido(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        // Mínimo 3 caracteres, apenas alfanuméricos e underscore
        String regex = "^[a-zA-Z0-9_]{3,50}$";
        return username.matches(regex);
    }

    /**
     * Valida senha
     */
    public static boolean isSenhaValida(String senha) {
        if (senha == null || senha.length() < 6) {
            return false;
        }

        return true;
    }
}
