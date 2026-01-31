package com.supermercado.controller;

import com.supermercado.model.Produto;
import com.supermercado.service.ProdutoService;
import com.supermercado.util.FormatadorUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BuscaProdutoController {

    @FXML
    private TextField filtroField;
    @FXML
    private TableView<Produto> produtosTableView;
    @FXML
    private TableColumn<Produto, String> colCodigo;
    @FXML
    private TableColumn<Produto, String> colNome;
    @FXML
    private TableColumn<Produto, String> colPreco;
    @FXML
    private TableColumn<Produto, Integer> colEstoque;
    @FXML
    private TextField quantidadeField;

    private final ProdutoService produtoService;
    private ObservableList<Produto> masterData = FXCollections.observableArrayList();
    private Produto produtoSelecionado;
    private int quantidade = 1;

    @Autowired
    public BuscaProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoBarras"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colPreco.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                FormatadorUtil.formatarMoeda(cellData.getValue().getPrecoVenda())));
        colEstoque.setCellValueFactory(new PropertyValueFactory<>("quantidadeEstoque"));

        masterData.setAll(produtoService.listarTodos());
        FilteredList<Produto> filteredData = new FilteredList<>(masterData, p -> true);

        filtroField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(produto -> {
                if (newValue == null || newValue.isEmpty())
                    return true;
                String lowerCaseFilter = newValue.toLowerCase();
                if (produto.getNome().toLowerCase().contains(lowerCaseFilter))
                    return true;
                if (produto.getCodigoBarras().toLowerCase().contains(lowerCaseFilter))
                    return true;
                return false;
            });
        });

        produtosTableView.setItems(filteredData);

        produtosTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                handleAdicionar();
            }
        });
    }

    @FXML
    private void handleAdicionar() {
        produtoSelecionado = produtosTableView.getSelectionModel().getSelectedItem();
        if (produtoSelecionado == null) {
            return;
        }

        try {
            quantidade = Integer.parseInt(quantidadeField.getText());
            if (quantidade <= 0)
                throw new NumberFormatException();

            closeStage();
        } catch (NumberFormatException e) {
            // Silently fail or show alert
        }
    }

    @FXML
    private void handleCancelar() {
        produtoSelecionado = null;
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) filtroField.getScene().getWindow();
        stage.close();
    }

    public Produto getProdutoSelecionado() {
        return produtoSelecionado;
    }

    public int getQuantidade() {
        return quantidade;
    }
}
