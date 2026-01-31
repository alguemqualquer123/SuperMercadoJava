package com.supermercado.service;

import com.supermercado.model.*;
import com.supermercado.repository.VendaRepository;
import com.supermercado.util.Validador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Service para lógica de negócio de Venda
 */
@Service
public class VendaService {

    private static final Logger logger = LoggerFactory.getLogger(VendaService.class);
    private final VendaRepository vendaRepository;
    private final ProdutoService produtoService;
    private final LogService logService;

    @Autowired
    public VendaService(VendaRepository vendaRepository, ProdutoService produtoService, LogService logService) {
        this.vendaRepository = vendaRepository;
        this.produtoService = produtoService;
        this.logService = logService;
    }

    /**
     * Cria uma nova venda
     */
    public Venda criarNovaVenda(Usuario usuario) {
        logger.debug("Criando nova venda para usuário: {}", usuario.getNome());

        Venda venda = new Venda();
        venda.setUsuario(usuario);
        venda.setStatus(Venda.StatusVenda.ABERTA);

        return venda;
    }

    /**
     * Adiciona item à venda
     */
    public void adicionarItem(Venda venda, String codigoBarras, int quantidade) {
        logger.debug("Adicionando item à venda: código {}, quantidade {}", codigoBarras, quantidade);

        if (venda.getStatus() != Venda.StatusVenda.ABERTA) {
            throw new IllegalStateException("Não é possível adicionar itens a uma venda finalizada ou cancelada");
        }

        // Busca o produto
        Optional<Produto> optProduto = produtoService.buscarPorCodigoBarras(codigoBarras);
        if (optProduto.isEmpty()) {
            throw new IllegalArgumentException("Produto não encontrado: " + codigoBarras);
        }

        Produto produto = optProduto.get();

        if (!produto.getAtivo()) {
            throw new IllegalStateException("Produto inativo: " + produto.getNome());
        }

        // Verifica estoque
        if (produto.getQuantidadeEstoque() < quantidade) {
            throw new IllegalStateException(
                    String.format("Estoque insuficiente! Disponível: %d, Solicitado: %d",
                            produto.getQuantidadeEstoque(), quantidade));
        }

        // Verifica se produto já está na venda
        Optional<ItemVenda> itemExistente = venda.getItens().stream()
                .filter(i -> i.getCodigoBarras().equals(codigoBarras))
                .findFirst();

        if (itemExistente.isPresent()) {
            // Atualiza quantidade
            ItemVenda item = itemExistente.get();
            int novaQuantidade = item.getQuantidade() + quantidade;

            if (produto.getQuantidadeEstoque() < novaQuantidade) {
                throw new IllegalStateException("Estoque insuficiente para a quantidade total");
            }

            item.setQuantidade(novaQuantidade);
        } else {
            // Adiciona novo item
            ItemVenda novoItem = new ItemVenda(produto, quantidade);
            venda.adicionarItem(novoItem);
        }

        logger.info("Item adicionado à venda: {}", produto.getNome());
    }

    /**
     * Remove item da venda
     */
    public void removerItem(Venda venda, ItemVenda item) {
        logger.debug("Removendo item da venda: {}", item.getNomeProduto());

        if (venda.getStatus() != Venda.StatusVenda.ABERTA) {
            throw new IllegalStateException("Não é possível remover itens de uma venda finalizada ou cancelada");
        }

        venda.removerItem(item);
        logger.info("Item removido da venda: {}", item.getNomeProduto());
    }

    /**
     * Aplica desconto percentual à venda
     */
    public void aplicarDesconto(Venda venda, BigDecimal percentual) {
        logger.debug("Aplicando desconto de {}% à venda", percentual);

        if (percentual.compareTo(BigDecimal.ZERO) < 0 || percentual.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Desconto deve estar entre 0% e 100%");
        }

        venda.setDescontoPercentual(percentual);
        venda.recalcularTotais();

        logger.info("Desconto aplicado: {}%", percentual);
    }

    /**
     * Finaliza a venda
     */
    @Transactional
    public Venda finalizarVenda(Venda venda, String formaPagamento, BigDecimal valorPago) {
        logger.debug("Finalizando venda");

        Validador.validarVenda(venda);

        if (venda.getItens().isEmpty()) {
            throw new IllegalStateException("Não é possível finalizar uma venda sem itens");
        }

        if (valorPago.compareTo(venda.getTotal()) < 0) {
            throw new IllegalArgumentException("Valor pago insuficiente");
        }

        // Finaliza a venda
        venda.finalizar(formaPagamento, valorPago);

        // Salva a venda
        Venda vendaSalva = vendaRepository.save(venda);

        // Baixa estoque dos produtos
        for (ItemVenda item : vendaSalva.getItens()) {
            produtoService.atualizarEstoque(item.getProduto().getId(), -item.getQuantidade());
        }

        logger.info("Venda finalizada com sucesso: ID {}, Total: {}",
                vendaSalva.getId(), vendaSalva.getTotal());

        // Registra log detalhado da venda
        Usuario usuario = vendaSalva.getUsuario();
        if (usuario != null) {
            String descricao = String.format("Venda finalizada - Total: R$ %.2f | Forma Pagamento: %s | Valor Pago: R$ %.2f | Troco: R$ %.2f | Itens: %d",
                    vendaSalva.getTotal(), formaPagamento, valorPago, vendaSalva.getTroco(), vendaSalva.getItens().size());
            logService.registrarLog(usuario, LogAcao.TipoAcao.VENDA_FINALIZADA, "Venda", vendaSalva.getId(), descricao);
        }

        return vendaSalva;
    }

    /**
     * Cancela a venda
     */
    @Transactional
    public void cancelarVenda(Venda venda) {
        logger.debug("Cancelando venda ID {}", venda.getId());

        venda.cancelar();
        vendaRepository.save(venda);

        logger.info("Venda cancelada: ID {}", venda.getId());
    }

    /**
     * Busca vendas do dia
     */
    public List<Venda> buscarVendasDoDia(LocalDate data) {
        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.atTime(LocalTime.MAX);
        return vendaRepository.findVendasFinalizadasDoDia(inicio, fim);
    }

    /**
     * Busca vendas por período
     */
    public List<Venda> buscarVendasPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(LocalTime.MAX);
        return vendaRepository.findByDataVendaBetweenOrderByDataVendaDesc(inicio, fim);
    }

    /**
     * Calcula total vendido no período
     */
    public BigDecimal calcularTotalPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(LocalTime.MAX);
        return vendaRepository.calcularTotalPeriodo(inicio, fim);
    }

    /**
     * Conta vendas do período
     */
    public long contarVendasPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(LocalTime.MAX);
        return vendaRepository.countVendasPeriodo(inicio, fim);
    }

    /**
     * Busca produtos mais vendidos
     */
    public List<Object[]> buscarProdutosMaisVendidos(LocalDate dataInicio, LocalDate dataFim, int limit) {
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(LocalTime.MAX);
        return vendaRepository.findProdutosMaisVendidos(inicio, fim, PageRequest.of(0, limit));
    }
}
