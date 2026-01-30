package com.supermercado.controller;

import com.supermercado.SuperMercadoApp;
import com.supermercado.model.*;
import com.supermercado.service.ProdutoService;
import com.supermercado.service.VendaService;
import com.supermercado.service.UsuarioService;
import com.supermercado.service.SessaoService;
import com.supermercado.util.*;
import com.supermercado.service.RelatorioService;
import java.util.List;
import java.util.Optional;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller principal para o PDV
 */
@Component
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    // Services
    private final UsuarioService usuarioService;
    private final ProdutoService produtoService;
    private final VendaService vendaService;
    private final SessaoService sessaoService;
    private final RelatorioService relatorioService;

    // Toolbar
    @FXML
    private Label usuarioLabel;
    @FXML
    private Label dataHoraLabel;

    // PDV Fields
    @FXML
    private TextField codigoBarrasField;
    @FXML
    private TextField quantidadeField;
    @FXML
    private TextField descontoField;

    // Tabela
    @FXML
    private TableView<ItemVenda> itensTableView;
    @FXML
    private TableColumn<ItemVenda, String> colCodigo;
    @FXML
    private TableColumn<ItemVenda, String> colNome;
    @FXML
    private TableColumn<ItemVenda, Integer> colQuantidade;
    @FXML
    private TableColumn<ItemVenda, String> colPrecoUnitario;
    @FXML
    private TableColumn<ItemVenda, String> colSubtotal;
    @FXML
    private TableColumn<ItemVenda, Void> colAcoes;

    // Labels
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private Button finalizarButton;

    // Data
    private ObservableList<ItemVenda> itens = FXCollections.observableArrayList();
    private Venda vendaAtual;

    @Autowired
    public MainController(UsuarioService usuarioService, ProdutoService produtoService, VendaService vendaService,
            SessaoService sessaoService, RelatorioService relatorioService) {
        this.usuarioService = usuarioService;
        this.produtoService = produtoService;
        this.vendaService = vendaService;
        this.sessaoService = sessaoService;
        this.relatorioService = relatorioService;
    }

    @FXML
    public void initialize() {
        logger.debug("Inicializando MainController");

        // Atualiza informações do usuário
        Usuario usuario = usuarioService.getUsuarioLogado();
        if (usuario != null) {
            usuarioLabel.setText("Usuário: " + usuario.getNome() + " (" + usuario.getPerfil().getNome() + ")");
        }

        // Inicia relógio
        iniciarRelogio();

        // Configura tabela
        configurarTabela();

        // Cria nova venda
        novaVenda();

        // Foco no campo de código de barras
        Platform.runLater(() -> {
            if (codigoBarrasField != null) {
                codigoBarrasField.requestFocus();
            }
        });

        // Atalhos de teclado
        Platform.runLater(this::configurarAtalhos);
    }

    private void iniciarRelogio() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            dataHoraLabel.setText(LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void configurarTabela() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoBarras"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nomeProduto"));
        colQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));

        colPrecoUnitario.setCellValueFactory(cellData -> new SimpleStringProperty(
                FormatadorUtil.formatarMoeda(cellData.getValue().getPrecoUnitario())));

        colSubtotal.setCellValueFactory(
                cellData -> new SimpleStringProperty(FormatadorUtil.formatarMoeda(cellData.getValue().getSubtotal())));

        // Coluna de ações (botão remover)
        colAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnRemover = new Button("Remover");

            {
                btnRemover.getStyleClass().add("btn-danger-small");
                btnRemover.setOnAction(event -> {
                    ItemVenda item = getTableView().getItems().get(getIndex());
                    removerItem(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnRemover);
            }
        });

        itensTableView.setItems(itens);
    }

    private void configurarAtalhos() {
        if (finalizarButton.getScene() != null) {
            finalizarButton.getScene().setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case F11:
                        handleAlternarTelaCheia();
                        break;
                    case F12:
                        handleFinalizarVenda();
                        break;
                    case F2:
                        handleBuscarProduto();
                        break;
                    case F4:
                        handleLimparVenda();
                        break;
                    default:
                        break;
                }
            });
        }
    }

    private void novaVenda() {
        vendaAtual = vendaService.criarNovaVenda(usuarioService.getUsuarioLogado());
        itens.clear();
        atualizarTotais();
        codigoBarrasField.clear();
        quantidadeField.setText("1");
        descontoField.setText("0");
        codigoBarrasField.requestFocus();
        logger.debug("Nova venda criada");
    }

    @FXML
    private void handleAdicionarProduto() {
        String codigo = codigoBarrasField.getText().trim();
        String qtdStr = quantidadeField.getText().trim();

        if (codigo.isEmpty()) {
            DialogUtil.showWarning("Atenção", "Digite o código de barras do produto");
            return;
        }

        try {
            int quantidade = Integer.parseInt(qtdStr.isEmpty() ? "1" : qtdStr);

            if (quantidade <= 0) {
                DialogUtil.showWarning("Atenção", "Quantidade deve ser maior que zero");
                return;
            }

            vendaService.adicionarItem(vendaAtual, codigo, quantidade);
            itens.setAll(vendaAtual.getItens());
            atualizarTotais();

            codigoBarrasField.clear();
            quantidadeField.setText("1");
            codigoBarrasField.requestFocus();

            logger.info("Produto adicionado: {}", codigo);

        } catch (NumberFormatException e) {
            DialogUtil.showError("Erro", "Quantidade inválida");
        } catch (Exception e) {
            DialogUtil.showException("Erro ao adicionar produto", e);
            logger.error("Erro ao adicionar produto", e);
        }
    }

    @FXML
    private void handleBuscarProduto() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Buscar Produto");
        dialog.setHeaderText("Digite o nome ou parte do nome do produto:");
        dialog.setContentText("Nome:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(nome -> {
            List<Produto> produtos = produtoService.buscarPorNome(nome);
            if (produtos.isEmpty()) {
                DialogUtil.showInfo("Busca", "Nenhum produto encontrado com o nome: " + nome);
            } else {
                // Seleciona o primeiro encontrado para simplificar o PDV
                Produto p = produtos.get(0);
                codigoBarrasField.setText(p.getCodigoBarras());
                quantidadeField.requestFocus();
            }
        });
    }

    @FXML
    private void handleRemoverItem() {
        ItemVenda item = itensTableView.getSelectionModel().getSelectedItem();
        if (item != null) {
            removerItem(item);
        } else {
            DialogUtil.showWarning("Atenção", "Selecione um item para remover");
        }
    }

    private void removerItem(ItemVenda item) {
        if (DialogUtil.showConfirmation("Confirmar",
                "Deseja remover o item: " + item.getNomeProduto() + "?")) {
            vendaService.removerItem(vendaAtual, item);
            itens.setAll(vendaAtual.getItens());
            atualizarTotais();
        }
    }

    @FXML
    private void handleAplicarDesconto() {
        try {
            BigDecimal desconto = new BigDecimal(descontoField.getText());
            vendaService.aplicarDesconto(vendaAtual, desconto);
            atualizarTotais();
        } catch (NumberFormatException e) {
            DialogUtil.showError("Erro", "Desconto inválido");
        } catch (Exception e) {
            DialogUtil.showException("Erro ao aplicar desconto", e);
        }
    }

    @FXML
    private void handleLimparVenda() {
        if (itens.isEmpty()) {
            return;
        }

        if (DialogUtil.showConfirmation("Confirmar", "Deseja limpar a venda atual?")) {
            novaVenda();
        }
    }

    @FXML
    private void handleFinalizarVenda() {
        if (itens.isEmpty()) {
            DialogUtil.showWarning("Atenção", "Adicione itens à venda antes de finalizar");
            return;
        }

        try {
            // Abre diálogo de pagamento
            PagamentoDialog dialog = new PagamentoDialog(vendaAtual.getTotal());
            Optional<PagamentoDialog.ResultadoPagamento> result = dialog.showAndWait();

            if (result.isPresent()) {
                PagamentoDialog.ResultadoPagamento pagamento = result.get();

                Venda vendaFinalizada = vendaService.finalizarVenda(
                        vendaAtual,
                        pagamento.getFormaPagamento(),
                        pagamento.getValorPago());

                DialogUtil.showInfo("Sucesso",
                        String.format("Venda #%d finalizada com sucesso!\nTroco: %s",
                                vendaFinalizada.getId(),
                                FormatadorUtil.formatarMoeda(vendaFinalizada.getTroco())));

                // Pergunta se deseja imprimir comprovante
                if (DialogUtil.showConfirmation("Comprovante",
                        "Deseja gerar comprovante em PDF?")) {
                    gerarComprovante(vendaFinalizada);
                }

                novaVenda();
            }

        } catch (Exception e) {
            DialogUtil.showException("Erro ao finalizar venda", e);
            logger.error("Erro ao finalizar venda", e);
        }
    }

    private void gerarComprovante(Venda venda) {
        try {
            java.io.File arquivo = ComprovantePDF.gerarComprovante(venda);
            DialogUtil.showInfo("Comprovante Gerado",
                    "Comprovante salvo em:\n" + arquivo.getAbsolutePath());

            // Tenta abrir o arquivo
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(arquivo);
            }
        } catch (Exception e) {
            DialogUtil.showException("Erro ao gerar comprovante", e);
            logger.error("Erro ao gerar comprovante", e);
        }
    }

    private void atualizarTotais() {
        subtotalLabel.setText(FormatadorUtil.formatarMoeda(vendaAtual.getSubtotal()));
        totalLabel.setText(FormatadorUtil.formatarMoeda(vendaAtual.getTotal()));
    }

    // Menu Actions
    @FXML
    private void handleLogout() {
        if (DialogUtil.showConfirmation("Logout", "Deseja sair do sistema?")) {
            usuarioService.logout();
            SuperMercadoApp.showLogin();
        }
    }

    @FXML
    private void handleExit() {
        if (DialogUtil.showConfirmation("Sair", "Deseja encerrar o sistema?")) {
            Platform.exit();
        }
    }

    @FXML
    private void handleProdutos() {
        if (sessaoService.temPermissao(Usuario.TipoPerfil.ADMINISTRADOR, Usuario.TipoPerfil.GERENTE)) {
            DialogUtil.showInfo("Módulo de Produtos",
                    "O gerenciamento de produtos pode ser feito via Banco de Dados ou aguarde a próxima atualização da UI.");
        } else {
            exibirAlerta("Acesso Negado", "Você não tem permissão para acessar o cadastro de produtos.");
        }
    }

    @FXML
    private void handleCategorias() {
        if (sessaoService.temPermissao(Usuario.TipoPerfil.ADMINISTRADOR, Usuario.TipoPerfil.GERENTE)) {
            DialogUtil.showInfo("Módulo de Categorias",
                    "O gerenciamento de categorias está disponível via persistência direta no momento.");
        } else {
            exibirAlerta("Acesso Negado", "Você não tem permissão para acessar o cadastro de categorias.");
        }
    }

    @FXML
    private void handleUsuarios() {
        if (sessaoService.temPermissao(Usuario.TipoPerfil.ADMINISTRADOR)) {
            DialogUtil.showInfo("Módulo de Usuários",
                    "O gerenciamento de usuários é restrito ao Administrador via console/DB nesta versão.");
        } else {
            exibirAlerta("Acesso Negado", "Apenas administradores podem gerenciar usuários.");
        }
    }

    @FXML
    private void handleRelatorioVendasDia() {
        try {
            String path = "relatorio_vendas_hoje.pdf";
            LocalDateTime inicio = LocalDateTime.now().with(java.time.LocalTime.MIN);
            LocalDateTime fim = LocalDateTime.now().with(java.time.LocalTime.MAX);
            relatorioService.gerarRelatorioFinanceiro(inicio, fim, path);
            DialogUtil.showInfo("Relatório Gerado", "Relatório de hoje gerado com sucesso em: " + path);
            abrirArquivo(new java.io.File(path));
        } catch (Exception e) {
            DialogUtil.showException("Erro ao gerar relatório", e);
        }
    }

    @FXML
    private void handleRelatorioVendasMes() {
        try {
            String path = "relatorio_vendas_mes.pdf";
            LocalDateTime inicio = LocalDateTime.now().withDayOfMonth(1).with(java.time.LocalTime.MIN);
            LocalDateTime fim = LocalDateTime.now().with(java.time.temporal.TemporalAdjusters.lastDayOfMonth())
                    .with(java.time.LocalTime.MAX);
            relatorioService.gerarRelatorioFinanceiro(inicio, fim, path);
            DialogUtil.showInfo("Relatório Gerado", "Relatório do mês gerado com sucesso em: " + path);
            abrirArquivo(new java.io.File(path));
        } catch (Exception e) {
            DialogUtil.showException("Erro ao gerar relatório", e);
        }
    }

    @FXML
    private void handleRelatorioProdutosMaisVendidos() {
        DialogUtil.showInfo("Produtos Mais Vendidos",
                "Consulte o Dashboard para visualizar o ranking de vendas em tempo real.");
    }

    @FXML
    private void handleDashboard() {
        if (sessaoService.temPermissao(Usuario.TipoPerfil.ADMINISTRADOR, Usuario.TipoPerfil.GERENTE)) {
            SuperMercadoApp.openInNewWindow("/fxml/Dashboard.fxml", "Dashboard", 800, 600);
        } else {
            exibirAlerta("Acesso Negado", "Você não tem permissão para acessar o Dashboard.");
        }
    }

    @FXML
    private void handleRelatorioFinanceiro() {
        if (sessaoService.temPermissao(Usuario.TipoPerfil.ADMINISTRADOR, Usuario.TipoPerfil.GERENTE)) {
            SuperMercadoApp.openInNewWindow("/fxml/Relatorios.fxml", "Relatórios Financeiros", 400, 300);
        } else {
            exibirAlerta("Acesso Negado", "Você não tem permissão para gerar relatórios.");
        }
    }

    @FXML
    private void handleFornecedores() {
        if (sessaoService.temPermissao(Usuario.TipoPerfil.ADMINISTRADOR, Usuario.TipoPerfil.GERENTE)) {
            exibirAlerta("Informação", "Módulo de Fornecedores em desenvolvimento.");
        } else {
            exibirAlerta("Acesso Negado", "Você não tem permissão para gerenciar fornecedores.");
        }
    }

    @FXML
    private void handleCompras() {
        if (sessaoService.temPermissao(Usuario.TipoPerfil.ADMINISTRADOR, Usuario.TipoPerfil.GERENTE)) {
            exibirAlerta("Informação", "Módulo de Compras (Entrada de Estoque) em desenvolvimento.");
        } else {
            exibirAlerta("Acesso Negado", "Você não tem permissão para realizar compras.");
        }
    }

    private void exibirAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    @FXML
    private void handleSobre() {
        DialogUtil.showInfo("Sobre",
                "SuperMercado PDV v1.0\n\n" +
                        "Sistema completo de Ponto de Venda\n" +
                        "Desenvolvido em JavaFX com Spring Boot e JPA/Hibernate");
    }

    private void handleAlternarTelaCheia() {
        if (finalizarButton.getScene() != null && finalizarButton.getScene().getWindow() instanceof Stage) {
            Stage stage = (Stage) finalizarButton.getScene().getWindow();
            stage.setFullScreen(!stage.isFullScreen());
        }
    }

    private void abrirArquivo(java.io.File arquivo) {
        try {
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(arquivo);
            }
        } catch (Exception e) {
            logger.error("Erro ao abrir arquivo", e);
        }
    }
}
