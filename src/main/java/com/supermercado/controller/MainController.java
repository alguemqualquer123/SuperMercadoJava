package com.supermercado.controller;

import com.supermercado.SuperMercadoApp;
import com.supermercado.model.*;
import com.supermercado.service.ProdutoService;
import com.supermercado.service.VendaService;
import com.supermercado.service.UsuarioService;
import com.supermercado.service.SessaoService;
import com.supermercado.service.ConfigService;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    private final ConfigService configService;

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
            SessaoService sessaoService, RelatorioService relatorioService, ConfigService configService) {
        this.usuarioService = usuarioService;
        this.produtoService = produtoService;
        this.vendaService = vendaService;
        this.sessaoService = sessaoService;
        this.relatorioService = relatorioService;
        this.configService = configService;
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

        // Carrega configurações (tema)
        Platform.runLater(this::carregarConfiguracoes);
    }

    private void carregarConfiguracoes() {
        if (finalizarButton.getScene() != null) {
            Scene scene = finalizarButton.getScene();
            Parent root = scene.getRoot();
            
            // Aplica tema salvo
            if (configService.isDarkMode()) {
                root.getStyleClass().add("dark-mode");
                logger.info("Tema dark mode carregado das configurações");
            }
        }
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
        String input = codigoBarrasField.getText().trim();
        String qtdStr = quantidadeField.getText().trim();

        if (input.isEmpty()) {
            DialogUtil.showWarning("Atenção", "Digite o código de barras do produto");
            return;
        }

        String codigo = input;
        int quantidade = 1;

        try {
            // Suporte para o formato Q*CODIGO (ex: 2*4242945258)
            if (input.contains("*")) {
                String[] partes = input.split("\\*");
                if (partes.length == 2) {
                    quantidade = Integer.parseInt(partes[0].trim());
                    codigo = partes[1].trim();
                } else {
                    DialogUtil.showWarning("Formato Inválido", "Use o formato QUANTIDADE*CODIGO");
                    return;
                }
            } else {
                quantidade = Integer.parseInt(qtdStr.isEmpty() ? "1" : qtdStr);
            }

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

            logger.info("Produto adicionado: {}, Quantidade: {}", codigo, quantidade);

        } catch (NumberFormatException e) {
            DialogUtil.showError("Erro", "Quantidade inválida no formato de entrada");
        } catch (Exception e) {
            DialogUtil.showException("Erro ao adicionar produto", e);
            logger.error("Erro ao adicionar produto", e);
        }
    }

    @FXML
    private void handleBuscarProduto() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BuscaProduto.fxml"));
            loader.setControllerFactory(SuperMercadoApp.getSpringContext()::getBean);
            Parent root = loader.load();

            BuscaProdutoController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Buscar Produto");
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            // Aplica tema atual ao diálogo
            if (finalizarButton.getScene().getRoot().getStyleClass().contains("dark-mode")) {
                root.getStyleClass().add("dark-mode");
            }

            stage.setScene(scene);
            stage.showAndWait();

            Produto p = controller.getProdutoSelecionado();
            if (p != null) {
                codigoBarrasField.setText(p.getCodigoBarras());
                quantidadeField.setText(String.valueOf(controller.getQuantidade()));
                handleAdicionarProduto();
            }
        } catch (Exception e) {
            logger.error("Erro ao abrir busca de produtos", e);
            DialogUtil.showException("Erro ao abrir busca", e);
        }
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
        abrirAdministracao(0);
    }

    @FXML
    private void handleCategorias() {
        abrirAdministracao(1);
    }

    @FXML
    private void handleUsuarios() {
        abrirAdministracao(2);
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
        abrirAdministracao(3);
    }

    @FXML
    private void handleCompras() {
        abrirAdministracao(4);
    }

    private void abrirAdministracao(int tabIndex) {
        if (!sessaoService.temPermissao(Usuario.TipoPerfil.ADMINISTRADOR, Usuario.TipoPerfil.GERENTE)) {
            exibirAlerta("Acesso Negado", "Você não tem permissão para esta funcionalidade.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Administracao.fxml"));
            loader.setControllerFactory(SuperMercadoApp.getSpringContext()::getBean);
            Parent root = loader.load();

            AdministracaoController controller = loader.getController();
            controller.setTab(tabIndex);

            Stage stage = new Stage();
            stage.setTitle("Administração - SuperMercado");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            if (finalizarButton.getScene().getRoot().getStyleClass().contains("dark-mode")) {
                root.getStyleClass().add("dark-mode");
            }

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            logger.error("Erro ao abrir administração", e);
            DialogUtil.showException("Erro ao abrir administração", e);
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
        DialogUtil.showInfo("Ajuda & Sobre",
                "SuperMercado PDV v1.5 - Sistema Profissional\n\n" +
                        "Funcionalidades Implementadas:\n" +
                        "- Ponto de Venda (PDV) com suporte a Q*CODIGO\n" +
                        "- Busca avançada de produtos com filtros\n" +
                        "- Gerenciamento de Estoque e Alertas de Nível Baixo\n" +
                        "- Cadastro de Produtos, Categorias e Usuários\n" +
                        "- Gestão de Fornecedores e Entrada de Mercadorias\n" +
                        "- Relatórios Financeiros e Ranking de Produtos\n" +
                        "- Suporte a Modo Escuro (Dark Mode)\n" +
                        "- Impressão de Comprovantes em PDF\n\n" +
                        "Tecnologias: JavaFX, Spring Boot, JPA/Hibernate, SQLite.");
    }

    private void handleAlternarTelaCheia() {
        if (finalizarButton.getScene() != null && finalizarButton.getScene().getWindow() instanceof Stage) {
            Stage stage = (Stage) finalizarButton.getScene().getWindow();
            stage.setFullScreen(!stage.isFullScreen());
        }
    }

    @FXML
    private void handleAlternarTema() {
        if (finalizarButton.getScene() != null) {
            Scene scene = finalizarButton.getScene();
            Parent root = scene.getRoot();
            boolean isDarkMode = root.getStyleClass().contains("dark-mode");
            
            if (isDarkMode) {
                root.getStyleClass().remove("dark-mode");
                configService.setDarkMode(false);
                logger.info("Tema alterado para Light Mode e salvo");
            } else {
                root.getStyleClass().add("dark-mode");
                configService.setDarkMode(true);
                logger.info("Tema alterado para Dark Mode e salvo");
            }
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
