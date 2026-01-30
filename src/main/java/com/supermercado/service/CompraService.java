package com.supermercado.service;

import com.supermercado.model.Compra;
import com.supermercado.model.ItemCompra;
import com.supermercado.model.Produto;
import com.supermercado.repository.CompraRepository;
import com.supermercado.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Transactional
    public Compra salvarCompra(Compra compra) {
        // Atualiza o estoque dos produtos
        for (ItemCompra item : compra.getItens()) {
            Produto produto = item.getProduto();
            produto.adicionarEstoque(item.getQuantidade());
            // Opcional: Atualizar preço de custo do produto com base na última compra
            produto.setPrecoCusto(item.getPrecoUnitario());
            produtoRepository.save(produto);
        }
        return compraRepository.save(compra);
    }
}
