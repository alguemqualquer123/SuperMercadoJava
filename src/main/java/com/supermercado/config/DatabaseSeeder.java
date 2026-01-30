package com.supermercado.config;

import com.supermercado.model.Categoria;
import com.supermercado.model.Fornecedor;
import com.supermercado.model.Produto;
import com.supermercado.repository.CategoriaRepository;
import com.supermercado.repository.FornecedorRepository;
import com.supermercado.repository.ProdutoRepository;
//import com.supermercado.service.UsuarioService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.ConfigurableApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeder para popular o banco de dados com dados de teste iniciais
 */
@Configuration
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);
    private static ConfigurableApplicationContext springContext;
    private final CategoriaRepository categoriaRepository;
    private final ProdutoRepository produtoRepository;
    private final FornecedorRepository fornecedorRepository;

    public DatabaseSeeder(CategoriaRepository categoriaRepository,
            ProdutoRepository produtoRepository,
            FornecedorRepository fornecedorRepository) {
        this.categoriaRepository = categoriaRepository;
        this.produtoRepository = produtoRepository;
        this.fornecedorRepository = fornecedorRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Verificando necessidade de popular dados de teste...");



        logger.info("Quantidade de categorias | Ele senta " + categoriaRepository.count());
        if (categoriaRepository.count() == 0) {
            seedCategorias();
        }

        logger.info("Quantidade de fornecedores | Ele senta " + categoriaRepository.count());
        if (fornecedorRepository.count() == 0) {
            seedFornecedores();
        }

        logger.info("Quantidade de produtos | Ele senta " + categoriaRepository.count());
        if (produtoRepository.count() == 0) {
            seedProdutos();
        }

        // Cria usuário admin padrão se não existir
//        UsuarioService usuarioService;
//        usuarioService = springContext.<UsuarioService>getBean(UsuarioService.class);
//        usuarioService.criarUsuarioAdminSeNaoExistir();

    }

    private void seedCategorias() {
        logger.info("Populando categorias de teste...");
        List<Categoria> categorias = new ArrayList<>();
        categorias.add(new Categoria("Alimentos", "Produtos alimentícios em geral"));
        categorias.add(new Categoria("Bebidas", "Bebidas alcoólicas e não alcoólicas"));
        categorias.add(new Categoria("Limpeza", "Produtos de limpeza para casa"));
        categorias.add(new Categoria("Higiene", "Produtos de higiene pessoal"));
        categorias.add(new Categoria("Hortifruti", "Frutas, legumes e verduras"));
        categorias.add(new Categoria("Açougue", "Carnes e derivados"));

        categoriaRepository.saveAll(categorias);
        logger.info("{} categorias cadastradas.", categorias.size());
    }

    private void seedFornecedores() {
        logger.info("Populando fornecedores de teste...");
        List<Fornecedor> fornecedores = new ArrayList<>();

        Fornecedor f1 = new Fornecedor("Distribuidora Alimentos S.A.", "12.345.678/0001-90");
        f1.setTelefone("(11) 4002-8922");
        f1.setEmail("vendas@distalimentos.com");
        fornecedores.add(f1);

        Fornecedor f2 = new Fornecedor("Bebidas Brasil Ltda", "98.765.432/0001-10");
        f2.setTelefone("(11) 3344-5566");
        f2.setEmail("contato@bebibrasil.com.br");
        fornecedores.add(f2);

        Fornecedor f3 = new Fornecedor("Limpeza Total Distribuidor", "45.678.912/0001-33");
        f3.setTelefone("(11) 2233-4455");
        fornecedores.add(f3);

        fornecedorRepository.saveAll(fornecedores);
        logger.info("{} fornecedores cadastrados.", fornecedores.size());
    }

    private void seedProdutos() {
        logger.info("Populando produtos de teste...");

        Categoria alimentos = categoriaRepository.findByNome("Alimentos").orElse(null);
        Categoria bebidas = categoriaRepository.findByNome("Bebidas").orElse(null);
        Categoria limpeza = categoriaRepository.findByNome("Limpeza").orElse(null);
        Categoria higiene = categoriaRepository.findByNome("Higiene").orElse(null);
        Categoria hortifruti = categoriaRepository.findByNome("Hortifruti").orElse(null);
        Categoria acougue = categoriaRepository.findByNome("Açougue").orElse(null);

        List<Produto> produtos = new ArrayList<>();

        // Alimentos
        produtos.add(criarProduto("7891234560011", "Arroz Agulhinha 5kg", alimentos, 18.50, 25.90, 50, 10, "UN"));
        produtos.add(criarProduto("7891234560028", "Feijão Carioca 1kg", alimentos, 5.80, 8.50, 100, 20, "UN"));
        produtos.add(criarProduto("7891234560035", "Macarrão Espaguete 500g", alimentos, 2.50, 4.50, 80, 15, "UN"));
        produtos.add(criarProduto("7891234560042", "Óleo de Soja 900ml", alimentos, 4.20, 6.90, 60, 12, "UN"));

        // Bebidas
        produtos.add(criarProduto("7891234560059", "Leite Integral 1L", bebidas, 3.20, 4.80, 120, 24, "UN"));
        produtos.add(criarProduto("7891234560066", "Cerveja Heineken 330ml", bebidas, 4.50, 6.50, 200, 48, "UN"));
        produtos.add(criarProduto("7891234560073", "Refrigerante Coca-Cola 2L", bebidas, 6.50, 9.90, 100, 20, "UN"));
        produtos.add(criarProduto("7891234560080", "Água Mineral 500ml", bebidas, 0.80, 2.00, 300, 50, "UN"));

        // Limpeza
        produtos.add(criarProduto("7891234560097", "Detergente Líquido 500ml", limpeza, 1.50, 2.50, 150, 30, "UN"));
        produtos.add(criarProduto("7891234560103", "Desinfetante Pinho 1L", limpeza, 4.80, 8.90, 40, 10, "UN"));
        produtos.add(criarProduto("7891234560110", "Sabão em Pó 1kg", limpeza, 8.50, 14.90, 50, 10, "UN"));

        // Higiene
        produtos.add(criarProduto("7891234560127", "Sabonete em Barra 90g", higiene, 1.10, 2.20, 200, 40, "UN"));
        produtos.add(criarProduto("7891234560134", "Creme Dental 90g", higiene, 2.50, 4.50, 100, 20, "UN"));
        produtos.add(criarProduto("7891234560141", "Shampoo Anticaspa 400ml", higiene, 12.00, 19.90, 30, 5, "UN"));

        // Hortifruti
        produtos.add(criarProduto("7891234560158", "Banana Prata kg", hortifruti, 3.50, 5.90, 20, 5, "KG"));
        produtos.add(criarProduto("7891234560165", "Batata Inglesa kg", hortifruti, 2.80, 4.90, 50, 10, "KG"));
        produtos.add(criarProduto("7891234560172", "Tomate Italiano kg", hortifruti, 4.50, 7.90, 30, 8, "KG"));

        // Açougue
        produtos.add(criarProduto("7891234560189", "Picanha Bovina kg", acougue, 65.00, 89.90, 15, 3, "KG"));
        produtos.add(criarProduto("7891234560196", "Frango Inteiro kg", acougue, 8.50, 14.50, 40, 10, "KG"));
        produtos.add(criarProduto("7891234560202", "Linguiça Toscana kg", acougue, 12.50, 22.90, 25, 5, "KG"));

        produtoRepository.saveAll(produtos);
        logger.info("{} produtos cadastrados.", produtos.size());
    }

    private Produto criarProduto(String cBarras, String nome, Categoria cat, double pCusto, double pVenda, int qEstoque,
            int eMinimo, String uMedida) {
        Produto p = new Produto();
        p.setCodigoBarras(cBarras);
        p.setNome(nome);
        p.setCategoria(cat);
        p.setPrecoCusto(BigDecimal.valueOf(pCusto));
        p.setPrecoVenda(BigDecimal.valueOf(pVenda));
        p.setQuantidadeEstoque(qEstoque);
        p.setEstoqueMinimo(eMinimo);
        p.setUnidadeMedida(uMedida);
        p.setAtivo(true);
        return p;
    }
}
