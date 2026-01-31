package com.supermercado.controller;

import com.supermercado.model.*;
import com.supermercado.service.*;
import com.supermercado.util.DialogUtil;
import com.supermercado.util.FormatadorUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class AdministracaoController {

    @FXML
    private TabPane mainTabPane;


    @Autowired
    private ProdutoService produtoService;
    @Autowired
    private CategoriaService categoriaService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private CompraService compraService;
    @Autowired
    private FornecedorService fornecedorService;
    @Autowired
    private LogService logService;


    @FXML
    private TextField filtroProdutoField;
    @FXML
    private TableView<Produto> produtosTableView;
    @FXML
    private TableColumn<Produto, Long> colProdId;
    @FXML
    private TableColumn<Produto, String> colProdCodigo;
    @FXML
    private TableColumn<Produto, String> colProdNome;
    @FXML
    private TableColumn<Produto, String> colProdCategoria;
    @FXML
    private TableColumn<Produto, String> colProdPrecoVenda;
    @FXML
    private TableColumn<Produto, Integer> colProdEstoque;
    @FXML
    private TableColumn<Produto, Integer> colProdEstoqueMin;


    @FXML
    private TableView<Categoria> categoriasTableView;
    @FXML
    private TableColumn<Categoria, Long> colCatId;
    @FXML
    private TableColumn<Categoria, String> colCatNome;
    @FXML
    private TableColumn<Categoria, String> colCatDesc;


    @FXML
    private TableView<Usuario> usuariosTableView;
    @FXML
    private TableColumn<Usuario, Long> colUserId;
    @FXML
    private TableColumn<Usuario, String> colUserNome;
    @FXML
    private TableColumn<Usuario, String> colUserLogin;
    @FXML
    private TableColumn<Usuario, String> colUserPerfil;
    @FXML
    private TableColumn<Usuario, Boolean> colUserAtivo;


    @FXML
    private TableView<Fornecedor> fornecedoresTableView;
    @FXML
    private TableColumn<Fornecedor, Long> colForId;
    @FXML
    private TableColumn<Fornecedor, String> colForNome;
    @FXML
    private TableColumn<Fornecedor, String> colForCnpj;
    @FXML
    private TableColumn<Fornecedor, String> colForEmail;
    @FXML
    private TableColumn<Fornecedor, String> colForFone;


    @FXML
    private TableView<Compra> entradasTableView;
    @FXML
    private TableColumn<Compra, Long> colEntId;
    @FXML
    private TableColumn<Compra, String> colEntData;
    @FXML
    private TableColumn<Compra, String> colEntFornecedor;
    @FXML
    private TableColumn<Compra, String> colEntTotal;
    @FXML
    private TableColumn<Compra, String> colEntStatus;


    @FXML
    private TableView<LogAcao> logsTableView;
    @FXML
    private TableColumn<LogAcao, String> colLogData;
    @FXML
    private TableColumn<LogAcao, String> colLogUsuario;
    @FXML
    private TableColumn<LogAcao, String> colLogAcao;
    @FXML
    private TableColumn<LogAcao, String> colLogEntidade;
    @FXML
    private TableColumn<LogAcao, String> colLogDescricao;
    @FXML
    private TextField filtroLogField;

    @FXML
    public void initialize() {
        configurarTabelaProdutos();
        configurarTabelaCategorias();
        configurarTabelaUsuarios();
        configurarTabelaFornecedores();
        configurarTabelaEntradas();
        configurarTabelaLogs();

        carregarDados();
    }

    private void configurarTabelaProdutos() {
        colProdId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colProdCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoBarras"));
        colProdNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colProdCategoria.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getCategoria().getNome()));
        colProdPrecoVenda.setCellValueFactory(cellData -> new SimpleStringProperty(
                FormatadorUtil.formatarMoeda(cellData.getValue().getPrecoVenda())));
        colProdEstoque.setCellValueFactory(new PropertyValueFactory<>("quantidadeEstoque"));
        colProdEstoqueMin.setCellValueFactory(new PropertyValueFactory<>("estoqueMinimo"));


        colProdEstoque.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    Produto p = getTableView().getItems().get(getIndex());
                    if (p.isEstoqueBaixo()) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }

    private void configurarTabelaCategorias() {
        colCatId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCatNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCatDesc.setCellValueFactory(new PropertyValueFactory<>("descricao"));
    }

    private void configurarTabelaUsuarios() {
        colUserId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUserNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colUserLogin.setCellValueFactory(new PropertyValueFactory<>("username"));
        colUserPerfil
                .setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPerfil().getNome()));
        colUserAtivo.setCellValueFactory(new PropertyValueFactory<>("ativo"));
    }

    private void configurarTabelaFornecedores() {
        colForId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colForNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colForCnpj.setCellValueFactory(new PropertyValueFactory<>("cnpj"));
        colForEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colForFone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
    }

    private void configurarTabelaEntradas() {
        colEntId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colEntData.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDataCompra().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        colEntFornecedor.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getFornecedor().getNome()));
        colEntTotal.setCellValueFactory(
                cellData -> new SimpleStringProperty(
                        FormatadorUtil.formatarMoeda(cellData.getValue().getValorTotal())));

        colEntStatus.setCellValueFactory(cellData -> new SimpleStringProperty("CONCLUÍDA"));
    }

    private void configurarTabelaLogs() {
        colLogData.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
        colLogUsuario.setCellValueFactory(cellData -> {
            Usuario usuario = cellData.getValue().getUsuario();
            return new SimpleStringProperty(usuario != null ? usuario.getNome() + " (" + usuario.getUsername() + ")" : "Sistema");
        });
        colLogAcao.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAcao().toString()));
        colLogEntidade.setCellValueFactory(cellData -> {
            LogAcao log = cellData.getValue();
            String entidade = log.getEntidade();
            if (log.getEntidadeId() != null) {
                entidade += " #" + log.getEntidadeId();
            }
            return new SimpleStringProperty(entidade);
        });
        colLogDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
    }

    private void carregarDados() {
        ObservableList<Produto> prods = FXCollections.observableArrayList(produtoService.listarTodos());
        FilteredList<Produto> filteredProds = new FilteredList<>(prods, p -> true);
        filtroProdutoField.textProperty().addListener((obs, old, newValue) -> {
            filteredProds.setPredicate(p -> {
                if (newValue == null || newValue.isEmpty())
                    return true;
                String lower = newValue.toLowerCase();
                return p.getNome().toLowerCase().contains(lower) || p.getCodigoBarras().contains(lower);
            });
        });
        produtosTableView.setItems(filteredProds);

        categoriasTableView.setItems(FXCollections.observableArrayList(categoriaService.listarTodas()));
        usuariosTableView.setItems(FXCollections.observableArrayList(usuarioService.listarTodos()));

        fornecedoresTableView.setItems(FXCollections.observableArrayList(fornecedorService.listarTodos()));
        entradasTableView.setItems(FXCollections.observableArrayList(compraService.listarTodas()));
        

        carregarLogs();
    }

    private void carregarLogs() {
        if (logService != null && logsTableView != null) {
            ObservableList<LogAcao> logs = FXCollections.observableArrayList(logService.listarTodos());
            FilteredList<LogAcao> filteredLogs = new FilteredList<>(logs, l -> true);
            
            if (filtroLogField != null) {
                filtroLogField.textProperty().addListener((obs, old, newValue) -> {
                    filteredLogs.setPredicate(l -> {
                        if (newValue == null || newValue.isEmpty())
                            return true;
                        String lower = newValue.toLowerCase();
                        return (l.getDescricao() != null && l.getDescricao().toLowerCase().contains(lower)) ||
                               (l.getEntidade() != null && l.getEntidade().toLowerCase().contains(lower)) ||
                               (l.getUsuario() != null && l.getUsuario().getNome().toLowerCase().contains(lower)) ||
                               (l.getAcao() != null && l.getAcao().toString().toLowerCase().contains(lower));
                    });
                });
            }
            
            logsTableView.setItems(filteredLogs);
        }
    }

    @FXML
    private void handleAtualizarLogs() {
        carregarLogs();
        DialogUtil.showInfo("Sucesso", "Logs atualizados!");
    }

    private void recarregarDados() {
        carregarDados();
    }

    public void setTab(int index) {
        if (mainTabPane != null && index >= 0 && index < mainTabPane.getTabs().size()) {
            mainTabPane.getSelectionModel().select(index);
        }
    }

    /**
     * Método auxiliar para obter texto de TextField de forma segura
     */
    private String getTextSafely(TextField field) {
        return field.getText() != null ? field.getText().trim() : "";
    }

    /**
     * Método auxiliar para obter texto de TextArea de forma segura
     */
    private String getTextSafely(TextArea area) {
        return area.getText() != null ? area.getText().trim() : "";
    }


    @FXML
    private void handleNovoProduto() {
        mostrarDialogProduto(null);
    }

    @FXML
    private void handleEditarProduto() {
        Produto selecionado = produtosTableView.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            DialogUtil.showWarning("Aviso", "Selecione um produto para editar.");
            return;
        }
        mostrarDialogProduto(selecionado);
    }

    @FXML
    private void handleExcluirProduto() {
        Produto selecionado = produtosTableView.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            DialogUtil.showWarning("Aviso", "Selecione um produto para excluir.");
            return;
        }

        if (DialogUtil.showConfirmation("Confirmar Exclusão",
                "Deseja realmente desativar o produto \"" + selecionado.getNome() + "\"?")) {
            try {
                produtoService.desativar(selecionado.getId());
                DialogUtil.showInfo("Sucesso", "Produto desativado com sucesso!");
                recarregarDados();
            } catch (Exception e) {
                DialogUtil.showException("Erro ao excluir produto", e);
            }
        }
    }

    private void mostrarDialogProduto(Produto produto) {
        Dialog<Produto> dialog = new Dialog<>();
        dialog.setTitle(produto == null ? "Novo Produto" : "Editar Produto");
        dialog.setHeaderText(produto == null ? "Cadastrar novo produto" : "Editar produto existente");

        ButtonType salvarButtonType = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(salvarButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField codigoBarrasField = new TextField();
        codigoBarrasField.setPromptText("Código de barras");
        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome do produto");
        ComboBox<Categoria> categoriaCombo = new ComboBox<>();
        categoriaCombo.setItems(FXCollections.observableArrayList(categoriaService.listarAtivas()));
        categoriaCombo.setPromptText("Selecione a categoria");
        TextField precoCustoField = new TextField();
        precoCustoField.setPromptText("0.00");
        TextField precoVendaField = new TextField();
        precoVendaField.setPromptText("0.00");
        TextField estoqueField = new TextField();
        estoqueField.setPromptText("0");
        TextField estoqueMinField = new TextField();
        estoqueMinField.setPromptText("0");
        TextField unidadeField = new TextField();
        unidadeField.setPromptText("UN");
        CheckBox ativoCheck = new CheckBox("Ativo");

        if (produto != null) {
            codigoBarrasField.setText(produto.getCodigoBarras());
            nomeField.setText(produto.getNome());
            categoriaCombo.setValue(produto.getCategoria());
            precoCustoField.setText(produto.getPrecoCusto().toString());
            precoVendaField.setText(produto.getPrecoVenda().toString());
            estoqueField.setText(produto.getQuantidadeEstoque().toString());
            estoqueMinField.setText(produto.getEstoqueMinimo().toString());
            unidadeField.setText(produto.getUnidadeMedida());
            ativoCheck.setSelected(produto.getAtivo());
        }

        grid.add(new Label("Código de Barras:"), 0, 0);
        grid.add(codigoBarrasField, 1, 0);
        grid.add(new Label("Nome:"), 0, 1);
        grid.add(nomeField, 1, 1);
        grid.add(new Label("Categoria:"), 0, 2);
        grid.add(categoriaCombo, 1, 2);
        grid.add(new Label("Preço Custo:"), 0, 3);
        grid.add(precoCustoField, 1, 3);
        grid.add(new Label("Preço Venda:"), 0, 4);
        grid.add(precoVendaField, 1, 4);
        grid.add(new Label("Estoque:"), 0, 5);
        grid.add(estoqueField, 1, 5);
        grid.add(new Label("Estoque Mínimo:"), 0, 6);
        grid.add(estoqueMinField, 1, 6);
        grid.add(new Label("Unidade:"), 0, 7);
        grid.add(unidadeField, 1, 7);
        grid.add(ativoCheck, 1, 8);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == salvarButtonType) {
                try {
                    Produto p = produto != null ? produto : new Produto();
                    p.setCodigoBarras(getTextSafely(codigoBarrasField));
                    p.setNome(getTextSafely(nomeField));
                    p.setCategoria(categoriaCombo.getValue());
                    p.setPrecoCusto(new BigDecimal(getTextSafely(precoCustoField)));
                    p.setPrecoVenda(new BigDecimal(getTextSafely(precoVendaField)));
                    p.setQuantidadeEstoque(Integer.parseInt(getTextSafely(estoqueField)));
                    p.setEstoqueMinimo(Integer.parseInt(getTextSafely(estoqueMinField)));
                    String unidade = getTextSafely(unidadeField);
                    p.setUnidadeMedida(unidade.isEmpty() ? "UN" : unidade);
                    p.setAtivo(ativoCheck.isSelected());
                    return p;
                } catch (Exception e) {
                    DialogUtil.showException("Erro ao processar dados", e);
                    return null;
                }
            }
            return null;
        });

        Optional<Produto> result = dialog.showAndWait();
        result.ifPresent(p -> {
            try {
                if (produto == null) {
                    produtoService.salvar(p);
                    DialogUtil.showInfo("Sucesso", "Produto cadastrado com sucesso!");
                } else {
                    produtoService.atualizar(p);
                    DialogUtil.showInfo("Sucesso", "Produto atualizado com sucesso!");
                }
                recarregarDados();
            } catch (Exception e) {
                DialogUtil.showException("Erro ao salvar produto", e);
            }
        });
    }


    @FXML
    private void handleNovaCategoria() {
        mostrarDialogCategoria(null);
    }

    @FXML
    private void handleEditarCategoria() {
        Categoria selecionada = categoriasTableView.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            DialogUtil.showWarning("Aviso", "Selecione uma categoria para editar.");
            return;
        }
        mostrarDialogCategoria(selecionada);
    }

    private void mostrarDialogCategoria(Categoria categoria) {
        Dialog<Categoria> dialog = new Dialog<>();
        dialog.setTitle(categoria == null ? "Nova Categoria" : "Editar Categoria");
        dialog.setHeaderText(categoria == null ? "Cadastrar nova categoria" : "Editar categoria existente");

        ButtonType salvarButtonType = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(salvarButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome da categoria");
        TextArea descricaoArea = new TextArea();
        descricaoArea.setPromptText("Descrição (opcional)");
        descricaoArea.setPrefRowCount(3);
        CheckBox ativoCheck = new CheckBox("Ativa");

        if (categoria != null) {
            nomeField.setText(categoria.getNome());
            descricaoArea.setText(categoria.getDescricao());
            ativoCheck.setSelected(categoria.getAtivo());
        }

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(nomeField, 1, 0);
        grid.add(new Label("Descrição:"), 0, 1);
        grid.add(descricaoArea, 1, 1);
        grid.add(ativoCheck, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == salvarButtonType) {
                try {
                    Categoria c = categoria != null ? categoria : new Categoria();
                    c.setNome(getTextSafely(nomeField));
                    c.setDescricao(getTextSafely(descricaoArea));
                    c.setAtivo(ativoCheck.isSelected());
                    return c;
                } catch (Exception e) {
                    DialogUtil.showException("Erro ao processar dados", e);
                    return null;
                }
            }
            return null;
        });

        Optional<Categoria> result = dialog.showAndWait();
        result.ifPresent(c -> {
            try {
                if (categoria == null) {
                    categoriaService.salvar(c);
                    DialogUtil.showInfo("Sucesso", "Categoria cadastrada com sucesso!");
                } else {
                    categoriaService.atualizar(c);
                    DialogUtil.showInfo("Sucesso", "Categoria atualizada com sucesso!");
                }
                recarregarDados();
            } catch (Exception e) {
                DialogUtil.showException("Erro ao salvar categoria", e);
            }
        });
    }


    @FXML
    private void handleNovoUsuario() {
        mostrarDialogUsuario(null);
    }

    @FXML
    private void handleEditarUsuario() {
        Usuario selecionado = usuariosTableView.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            DialogUtil.showWarning("Aviso", "Selecione um usuário para editar.");
            return;
        }
        mostrarDialogUsuario(selecionado);
    }

    private void mostrarDialogUsuario(Usuario usuario) {
        Dialog<Usuario> dialog = new Dialog<>();
        dialog.setTitle(usuario == null ? "Novo Usuário" : "Editar Usuário");
        dialog.setHeaderText(usuario == null ? "Cadastrar novo usuário" : "Editar usuário existente");

        ButtonType salvarButtonType = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(salvarButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField usernameField = new TextField();
        usernameField.setPromptText("Nome de usuário");
        PasswordField senhaField = new PasswordField();
        senhaField.setPromptText("Senha");
        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome completo");
        TextField emailField = new TextField();
        emailField.setPromptText("Email (opcional)");
        ComboBox<Usuario.TipoPerfil> perfilCombo = new ComboBox<>();
        perfilCombo.setItems(FXCollections.observableArrayList(Usuario.TipoPerfil.values()));
        perfilCombo.setPromptText("Selecione o perfil");
        CheckBox ativoCheck = new CheckBox("Ativo");

        if (usuario != null) {
            usernameField.setText(usuario.getUsername());
            usernameField.setEditable(false); // Não permite alterar username
            senhaField.setPromptText("Deixe em branco para manter a senha atual");
            nomeField.setText(usuario.getNome());
            emailField.setText(usuario.getEmail());
            perfilCombo.setValue(usuario.getPerfil());
            ativoCheck.setSelected(usuario.getAtivo());
        }

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Senha:"), 0, 1);
        grid.add(senhaField, 1, 1);
        grid.add(new Label("Nome:"), 0, 2);
        grid.add(nomeField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Perfil:"), 0, 4);
        grid.add(perfilCombo, 1, 4);
        grid.add(ativoCheck, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == salvarButtonType) {
                try {
                    Usuario u = usuario != null ? usuario : new Usuario();
                    if (usuario == null) {
                        u.setUsername(getTextSafely(usernameField));
                        String senha = getTextSafely(senhaField);
                        if (senha.isEmpty()) {
                            DialogUtil.showError("Erro", "Senha é obrigatória para novos usuários!");
                            return null;
                        }
                        u.setPassword(senha);
                    } else {
                        String senha = getTextSafely(senhaField);
                        if (!senha.isEmpty()) {
                            u.setPassword(senha);
                        }
                    }
                    u.setNome(getTextSafely(nomeField));
                    String email = getTextSafely(emailField);
                    u.setEmail(email.isEmpty() ? null : email);
                    u.setPerfil(perfilCombo.getValue());
                    u.setAtivo(ativoCheck.isSelected());
                    return u;
                } catch (Exception e) {
                    DialogUtil.showException("Erro ao processar dados", e);
                    return null;
                }
            }
            return null;
        });

        Optional<Usuario> result = dialog.showAndWait();
        result.ifPresent(u -> {
            try {
                if (usuario == null) {
                    String senha = getTextSafely(senhaField);
                    usuarioService.criarUsuario(u.getUsername(), senha, u.getNome(), u.getEmail(), u.getPerfil());
                    DialogUtil.showInfo("Sucesso", "Usuário cadastrado com sucesso!");
                } else {
                    usuarioService.atualizar(u);
                    DialogUtil.showInfo("Sucesso", "Usuário atualizado com sucesso!");
                }
                recarregarDados();
            } catch (Exception e) {
                DialogUtil.showException("Erro ao salvar usuário", e);
            }
        });
    }

    // Ações para Fornecedores
    @FXML
    private void handleNovoFornecedor() {
        mostrarDialogFornecedor(null);
    }

    @FXML
    private void handleEditarFornecedor() {
        Fornecedor selecionado = fornecedoresTableView.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            DialogUtil.showWarning("Aviso", "Selecione um fornecedor para editar.");
            return;
        }
        mostrarDialogFornecedor(selecionado);
    }

    private void mostrarDialogFornecedor(Fornecedor fornecedor) {
        Dialog<Fornecedor> dialog = new Dialog<>();
        dialog.setTitle(fornecedor == null ? "Novo Fornecedor" : "Editar Fornecedor");
        dialog.setHeaderText(fornecedor == null ? "Cadastrar novo fornecedor" : "Editar fornecedor existente");

        ButtonType salvarButtonType = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(salvarButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nomeField = new TextField();
        nomeField.setPromptText("Nome fantasia");
        TextField cnpjField = new TextField();
        cnpjField.setPromptText("CNPJ");
        TextField telefoneField = new TextField();
        telefoneField.setPromptText("Telefone");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField enderecoField = new TextField();
        enderecoField.setPromptText("Endereço");
        CheckBox ativoCheck = new CheckBox("Ativo");

        if (fornecedor != null) {
            nomeField.setText(fornecedor.getNome());
            cnpjField.setText(fornecedor.getCnpj());
            telefoneField.setText(fornecedor.getTelefone());
            emailField.setText(fornecedor.getEmail());
            enderecoField.setText(fornecedor.getEndereco());
            ativoCheck.setSelected(fornecedor.getAtivo());
        }

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(nomeField, 1, 0);
        grid.add(new Label("CNPJ:"), 0, 1);
        grid.add(cnpjField, 1, 1);
        grid.add(new Label("Telefone:"), 0, 2);
        grid.add(telefoneField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Endereço:"), 0, 4);
        grid.add(enderecoField, 1, 4);
        grid.add(ativoCheck, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == salvarButtonType) {
                try {
                    Fornecedor f = fornecedor != null ? fornecedor : new Fornecedor();
                    f.setNome(getTextSafely(nomeField));
                    f.setCnpj(getTextSafely(cnpjField));
                    String telefone = getTextSafely(telefoneField);
                    f.setTelefone(telefone.isEmpty() ? null : telefone);
                    String email = getTextSafely(emailField);
                    f.setEmail(email.isEmpty() ? null : email);
                    String endereco = getTextSafely(enderecoField);
                    f.setEndereco(endereco.isEmpty() ? null : endereco);
                    f.setAtivo(ativoCheck.isSelected());
                    return f;
                } catch (Exception e) {
                    DialogUtil.showException("Erro ao processar dados", e);
                    return null;
                }
            }
            return null;
        });

        Optional<Fornecedor> result = dialog.showAndWait();
        result.ifPresent(f -> {
            try {
                if (fornecedor == null) {
                    fornecedorService.salvar(f);
                    DialogUtil.showInfo("Sucesso", "Fornecedor cadastrado com sucesso!");
                } else {
                    fornecedorService.atualizar(f);
                    DialogUtil.showInfo("Sucesso", "Fornecedor atualizado com sucesso!");
                }
                recarregarDados();
            } catch (Exception e) {
                DialogUtil.showException("Erro ao salvar fornecedor", e);
            }
        });
    }

    // Ações para Entradas
    @FXML
    private void handleNovaEntrada() {
        mostrarDialogNovaEntrada();
    }

    private void mostrarDialogNovaEntrada() {
        Dialog<Compra> dialog = new Dialog<>();
        dialog.setTitle("Nova Entrada de Mercadoria");
        dialog.setHeaderText("Lançar nova nota fiscal de entrada");

        ButtonType salvarButtonType = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(salvarButtonType, ButtonType.CANCEL);

        VBox vbox = new VBox(10);
        vbox.setPadding(new javafx.geometry.Insets(20));

        // Campos principais
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        ComboBox<Fornecedor> fornecedorCombo = new ComboBox<>();
        fornecedorCombo.setItems(FXCollections.observableArrayList(fornecedorService.listarAtivos()));
        fornecedorCombo.setPromptText("Selecione o fornecedor");
        TextField numeroNotaField = new TextField();
        numeroNotaField.setPromptText("Número da nota fiscal");

        grid.add(new Label("Fornecedor:"), 0, 0);
        grid.add(fornecedorCombo, 1, 0);
        grid.add(new Label("Nº Nota Fiscal:"), 0, 1);
        grid.add(numeroNotaField, 1, 1);

        // Tabela de itens
        TableView<ItemCompra> itensTable = new TableView<>();
        ObservableList<ItemCompra> itens = FXCollections.observableArrayList();

        TableColumn<ItemCompra, String> colProduto = new TableColumn<>("Produto");
        colProduto.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProduto().getNome()));
        colProduto.setPrefWidth(200);

        TableColumn<ItemCompra, Integer> colQtd = new TableColumn<>("Quantidade");
        colQtd.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colQtd.setPrefWidth(100);

        TableColumn<ItemCompra, String> colPreco = new TableColumn<>("Preço Unit.");
        colPreco.setCellValueFactory(cellData -> new SimpleStringProperty(
                FormatadorUtil.formatarMoeda(cellData.getValue().getPrecoUnitario())));
        colPreco.setPrefWidth(120);

        TableColumn<ItemCompra, String> colSubtotal = new TableColumn<>("Subtotal");
        colSubtotal.setCellValueFactory(cellData -> new SimpleStringProperty(
                FormatadorUtil.formatarMoeda(cellData.getValue().getSubtotal())));
        colSubtotal.setPrefWidth(120);

        itensTable.getColumns().add(colProduto);
        itensTable.getColumns().add(colQtd);
        itensTable.getColumns().add(colPreco);
        itensTable.getColumns().add(colSubtotal);
        itensTable.setItems(itens);
        itensTable.setPrefHeight(200);

        // Campos para adicionar item
        GridPane gridItem = new GridPane();
        gridItem.setHgap(10);
        gridItem.setVgap(10);

        ComboBox<Produto> produtoCombo = new ComboBox<>();
        produtoCombo.setItems(FXCollections.observableArrayList(produtoService.listarAtivos()));
        produtoCombo.setPromptText("Selecione o produto");
        TextField qtdField = new TextField();
        qtdField.setPromptText("Quantidade");
        TextField precoField = new TextField();
        precoField.setPromptText("Preço unitário");
        Button adicionarBtn = new Button("Adicionar Item");

        gridItem.add(new Label("Produto:"), 0, 0);
        gridItem.add(produtoCombo, 1, 0);
        gridItem.add(new Label("Quantidade:"), 0, 1);
        gridItem.add(qtdField, 1, 1);
        gridItem.add(new Label("Preço Unitário:"), 0, 2);
        gridItem.add(precoField, 1, 2);
        gridItem.add(adicionarBtn, 1, 3);

        // Label de total
        Label totalLabel = new Label("Total: R$ 0,00");
        totalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        adicionarBtn.setOnAction(e -> {
            try {
                if (produtoCombo.getValue() == null) {
                    DialogUtil.showWarning("Aviso", "Selecione um produto.");
                    return;
                }
                String qtdStr = getTextSafely(qtdField);
                String precoStr = getTextSafely(precoField);
                if (qtdStr.isEmpty() || precoStr.isEmpty()) {
                    DialogUtil.showWarning("Aviso", "Preencha quantidade e preço.");
                    return;
                }
                int qtd = Integer.parseInt(qtdStr);
                BigDecimal preco = new BigDecimal(precoStr);
                ItemCompra item = new ItemCompra(produtoCombo.getValue(), qtd, preco);
                itens.add(item);
                atualizarTotal(itens, totalLabel);
                produtoCombo.setValue(null);
                qtdField.clear();
                precoField.clear();
            } catch (Exception ex) {
                DialogUtil.showException("Erro ao adicionar item", ex);
            }
        });

        Button removerBtn = new Button("Remover Selecionado");
        removerBtn.setOnAction(e -> {
            ItemCompra selecionado = itensTable.getSelectionModel().getSelectedItem();
            if (selecionado != null) {
                itens.remove(selecionado);
                atualizarTotal(itens, totalLabel);
            }
        });

        vbox.getChildren().addAll(grid, new Label("Itens da Compra:"), itensTable, gridItem,
                new javafx.scene.layout.HBox(10, removerBtn, totalLabel));

        dialog.getDialogPane().setContent(vbox);
        dialog.setResizable(true);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == salvarButtonType) {
                try {
                    if (fornecedorCombo.getValue() == null) {
                        DialogUtil.showError("Erro", "Selecione um fornecedor!");
                        return null;
                    }
                    if (itens.isEmpty()) {
                        DialogUtil.showError("Erro", "Adicione pelo menos um item!");
                        return null;
                    }

                    Compra compra = new Compra();
                    compra.setFornecedor(fornecedorCombo.getValue());
                    compra.setUsuario(usuarioService.getUsuarioLogado());
                    compra.setNumeroNotaFiscal(getTextSafely(numeroNotaField));
                    compra.setDataCompra(java.time.LocalDateTime.now());

                    for (ItemCompra item : itens) {
                        compra.adicionarItem(item);
                    }

                    return compra;
                } catch (Exception e) {
                    DialogUtil.showException("Erro ao processar dados", e);
                    return null;
                }
            }
            return null;
        });

        Optional<Compra> result = dialog.showAndWait();
        result.ifPresent(compra -> {
            try {
                compraService.salvarCompra(compra);
                DialogUtil.showInfo("Sucesso", "Entrada de mercadoria registrada com sucesso!");
                recarregarDados();
            } catch (Exception e) {
                DialogUtil.showException("Erro ao salvar entrada", e);
            }
        });
    }

    private void atualizarTotal(ObservableList<ItemCompra> itens, Label totalLabel) {
        BigDecimal total = itens.stream()
                .map(ItemCompra::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalLabel.setText("Total: " + FormatadorUtil.formatarMoeda(total));
    }
}
