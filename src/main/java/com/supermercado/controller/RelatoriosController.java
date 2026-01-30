package com.supermercado.controller;

import com.supermercado.service.RelatorioService;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
public class RelatoriosController {

    @FXML
    private DatePicker dpInicio;
    @FXML
    private DatePicker dpFim;
    @FXML
    private Label lblStatus;

    @Autowired
    private RelatorioService relatorioService;

    @FXML
    private void gerarPDF() {
        try {
            LocalDate inicio = dpInicio.getValue();
            LocalDate fim = dpFim.getValue();

            if (inicio == null || fim == null) {
                lblStatus.setText("Selecione as datas!");
                return;
            }

            String path = "relatorio_vendas.pdf";
            relatorioService.gerarRelatorioFinanceiro(inicio.atStartOfDay(), fim.atTime(23, 59), path);

            lblStatus.setText("Relatório gerado em: " + path);

        } catch (Exception e) {
            e.printStackTrace();
            lblStatus.setText("Erro ao gerar relatório!");
        }
    }
}
