package com.supermercado.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade que representa uma venda realizada
 */
@Entity
@Table(name = "vendas", indexes = {
        @Index(name = "idx_data_venda", columnList = "dataVenda")
})
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_venda", nullable = false)
    private LocalDateTime dataVenda;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ItemVenda> itens = new ArrayList<>();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal desconto = BigDecimal.ZERO;

    @Column(name = "desconto_percentual", precision = 5, scale = 2)
    private BigDecimal descontoPercentual = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "forma_pagamento", length = 50)
    private String formaPagamento; // DINHEIRO, CARTAO_CREDITO, CARTAO_DEBITO, PIX

    @Column(name = "valor_pago", precision = 10, scale = 2)
    private BigDecimal valorPago = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal troco = BigDecimal.ZERO;

    @Column(length = 1000)
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusVenda status = StatusVenda.ABERTA;

    public Venda() {
    }

    public Venda(Long id, LocalDateTime dataVenda, Usuario usuario, List<ItemVenda> itens, BigDecimal subtotal,
            BigDecimal desconto, BigDecimal descontoPercentual, BigDecimal total, String formaPagamento,
            BigDecimal valorPago, BigDecimal troco, String observacoes, StatusVenda status) {
        this.id = id;
        this.dataVenda = dataVenda;
        this.usuario = usuario;
        this.itens = itens;
        this.subtotal = subtotal;
        this.desconto = desconto;
        this.descontoPercentual = descontoPercentual;
        this.total = total;
        this.formaPagamento = formaPagamento;
        this.valorPago = valorPago;
        this.troco = troco;
        this.observacoes = observacoes;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDateTime dataVenda) {
        this.dataVenda = dataVenda;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }

    public void setItens(List<ItemVenda> itens) {
        this.itens = itens;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getDescontoPercentual() {
        return descontoPercentual;
    }

    public void setDescontoPercentual(BigDecimal descontoPercentual) {
        this.descontoPercentual = descontoPercentual;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public void setValorPago(BigDecimal valorPago) {
        this.valorPago = valorPago;
    }

    public BigDecimal getTroco() {
        return troco;
    }

    public void setTroco(BigDecimal troco) {
        this.troco = troco;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public StatusVenda getStatus() {
        return status;
    }

    public void setStatus(StatusVenda status) {
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        if (dataVenda == null) {
            dataVenda = LocalDateTime.now();
        }
    }

    /**
     * Adiciona um item à venda
     */
    public void adicionarItem(ItemVenda item) {
        itens.add(item);
        item.setVenda(this);
        recalcularTotais();
    }

    /**
     * Remove um item da venda
     */
    public void removerItem(ItemVenda item) {
        itens.remove(item);
        item.setVenda(null);
        recalcularTotais();
    }

    /**
     * Recalcula os totais da venda
     */
    public void recalcularTotais() {
        subtotal = itens.stream()
                .map(ItemVenda::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Aplica desconto percentual se houver
        if (descontoPercentual.compareTo(BigDecimal.ZERO) > 0) {
            desconto = subtotal.multiply(descontoPercentual)
                    .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
        }

        total = subtotal.subtract(desconto);

        // Calcula troco se houver valor pago
        if (valorPago.compareTo(BigDecimal.ZERO) > 0) {
            troco = valorPago.subtract(total);
        }
    }

    /**
     * Finaliza a venda
     */
    public void finalizar(String formaPagamento, BigDecimal valorPago) {
        this.formaPagamento = formaPagamento;
        this.valorPago = valorPago;
        this.status = StatusVenda.FINALIZADA;
        recalcularTotais();
    }

    /**
     * Cancela a venda
     */
    public void cancelar() {
        this.status = StatusVenda.CANCELADA;
    }

    /**
     * Status da venda
     */
    public enum StatusVenda {
        ABERTA, FINALIZADA, CANCELADA
    }
}
