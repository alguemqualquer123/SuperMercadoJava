package com.supermercado.api;

import com.supermercado.model.Produto;
import com.supermercado.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoRestController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping
    public List<Produto> listar() {
        return produtoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Produto buscar(@PathVariable Long id) {
        return produtoRepository.findById(id).orElseThrow();
    }

    @GetMapping("/barcode/{barcode}")
    public Produto buscarPorCodigo(@PathVariable String barcode) {
        return produtoRepository.findByCodigoBarras(barcode).orElseThrow();
    }
}
