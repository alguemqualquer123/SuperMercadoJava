package com.supermercado.controller;

import com.supermercado.repository.ProdutoRepository;
import com.supermercado.repository.VendaRepository;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class DashboardController implements Initializable {

    @FXML
    private Label lblVendasHoje;
    @FXML
    private Label lblQtdVendas;
    @FXML
    private BarChart<String, Number> vendasChart;
    @FXML
    private PieChart estoqueChart;

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        carregarDados();
    }

    private void carregarDados() {
        LocalDateTime inicio = LocalDate.now().atStartOfDay();
        LocalDateTime fim = LocalDateTime.now();

        BigDecimal total = vendaRepository.calcularTotalPeriodo(inicio, fim);
        long qtd = vendaRepository.countVendasPeriodo(inicio, fim);

        lblVendasHoje.setText(String.format("R$ %.2f", total));
        lblQtdVendas.setText(String.valueOf(qtd));

        // Gráfico de Barras - Vendas por Produto (Ranking)
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Vendas por Produto");

        List<Object[]> maisVendidos = vendaRepository.findProdutosMaisVendidos(inicio, fim,
                org.springframework.data.domain.PageRequest.of(0, 5));

        for (Object[] obj : maisVendidos) {
            String nome = (String) obj[0];
            Number quantidade = (Number) obj[1];
            series.getData().add(new XYChart.Data<>(nome, quantidade));
        }

        vendasChart.getData().clear();
        vendasChart.getData().add(series);

        // Gráfico de Pizza - Status do Estoque
        long estoqueBaixo = produtoRepository.findComEstoqueBaixo().size();
        long estoqueNormal = produtoRepository.findAtivos().size() - estoqueBaixo;

        estoqueChart.getData().clear();
        estoqueChart.getData().add(new PieChart.Data("Normal (" + estoqueNormal + ")", estoqueNormal));
        estoqueChart.getData().add(new PieChart.Data("Baixo (" + estoqueBaixo + ")", estoqueBaixo));
    }
}
