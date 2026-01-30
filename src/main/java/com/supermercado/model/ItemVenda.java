package com.supermercado.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * Entidade que representa um item de uma venda
 */
@Entity
@Table(name = "itens_venda")
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id", nullable = false)
    private Venda venda;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @Column(name = "codigo_barras", nullable = false, length = 50)
    private String codigoBarras;

    @Column(name = "nome_produto", nullable = false, length = 200)
    private String nomeProduto;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "preco_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    @Column(precision = 10, scale = 2)
    private BigDecimal desconto = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    public ItemVenda() {
    }

    public ItemVenda(Long id, Venda venda, Produto produto, String codigoBarras, String nomeProduto, Integer quantidade,
            BigDecimal precoUnitario, BigDecimal desconto, BigDecimal subtotal) {
        this.id = id;
        this.venda = venda;
        this.produto = produto;
        this.codigoBarras = codigoBarras;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.desconto = desconto;
        this.subtotal = subtotal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    /**
     * Construtor para criar item a partir de produto
     */
    public ItemVenda(Produto produto, Integer quantidade) {
        this.produto = produto;
        this.codigoBarras = produto.getCodigoBarras();
        this.nomeProduto = produto.getNome();
        this.quantidade = quantidade;
        this.precoUnitario = produto.getPrecoVenda();
        this.desconto = BigDecimal.ZERO;
        calcularSubtotal();
    }

    /**
     * Calcula o subtotal do item
     */
    public void calcularSubtotal() {
        BigDecimal total = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
        subtotal = total.subtract(desconto);
    }

    /**
     * Define a quantidade e recalcula o subtotal
     */
    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
        calcularSubtotal();
    }

    /**
     * Define o desconto e recalcula o subtotal
     */
    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
        calcularSubtotal();
    }

    @PrePersist
    @PreUpdate
    protected void onSave() {
        calcularSubtotal();
    }
}
