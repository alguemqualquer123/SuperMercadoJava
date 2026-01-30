package com.supermercado.controller;

import com.supermercado.util.FormatadorUtil;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.math.BigDecimal;

/**
 * Diálogo para finalização de pagamento
 */
public class PagamentoDialog extends Dialog<PagamentoDialog.ResultadoPagamento> {

    private final BigDecimal totalVenda;
    private TextField valorPagoField;
    private ComboBox<String> formaPagamentoCombo;
    private Label trocoLabel;

    public PagamentoDialog(BigDecimal totalVenda) {
        this.totalVenda = totalVenda;

        setTitle("Finalizar Pagamento");
        setHeaderText("Total da Venda: " + FormatadorUtil.formatarMoeda(totalVenda));

        // Botões
        ButtonType finalizarButtonType = new ButtonType("Finalizar", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(finalizarButtonType, ButtonType.CANCEL);

        // Conteúdo
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        formaPagamentoCombo = new ComboBox<>();
        formaPagamentoCombo.getItems().addAll("DINHEIRO", "CARTÃO DÉBITO", "CARTÃO CRÉDITO", "PIX");
        formaPagamentoCombo.setValue("DINHEIRO");

        valorPagoField = new TextField();
        valorPagoField.setText(totalVenda.toString());
        valorPagoField.setPromptText("Valor pago");

        trocoLabel = new Label("R$ 0,00");
        trocoLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: green;");

        // Calcula troco ao digitar
        valorPagoField.textProperty().addListener((obs, oldVal, newVal) -> calcularTroco());

        grid.add(new Label("Forma de Pagamento:"), 0, 0);
        grid.add(formaPagamentoCombo, 1, 0);
        grid.add(new Label("Valor Pago:"), 0, 1);
        grid.add(valorPagoField, 1, 1);
        grid.add(new Label("Troco:"), 0, 2);
        grid.add(trocoLabel, 1, 2);

        getDialogPane().setContent(grid);

        // Converter resultado
        setResultConverter(dialogButton -> {
            if (dialogButton == finalizarButtonType) {
                try {
                    BigDecimal valorPago = FormatadorUtil.parseMoeda(valorPagoField.getText());

                    if (valorPago.compareTo(totalVenda) < 0) {
                        return null;
                    }

                    return new ResultadoPagamento(
                            formaPagamentoCombo.getValue(),
                            valorPago);
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        });

        // Validação
        Button btnFinalizar = (Button) getDialogPane().lookupButton(finalizarButtonType);
        btnFinalizar.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            try {
                BigDecimal valorPago = FormatadorUtil.parseMoeda(valorPagoField.getText());
                if (valorPago.compareTo(totalVenda) < 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erro");
                    alert.setHeaderText("Valor insuficiente");
                    alert.setContentText("O valor pago deve ser maior ou igual ao total da venda");
                    alert.showAndWait();
                    event.consume();
                }
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Valor inválido");
                alert.setContentText("Digite um valor válido");
                alert.showAndWait();
                event.consume();
            }
        });
    }

    private void calcularTroco() {
        try {
            BigDecimal valorPago = FormatadorUtil.parseMoeda(valorPagoField.getText());
            BigDecimal troco = valorPago.subtract(totalVenda);

            if (troco.compareTo(BigDecimal.ZERO) >= 0) {
                trocoLabel.setText(FormatadorUtil.formatarMoeda(troco));
                trocoLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: green;");
            } else {
                trocoLabel.setText(FormatadorUtil.formatarMoeda(troco.abs()) + " (FALTA)");
                trocoLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: red;");
            }
        } catch (Exception e) {
            trocoLabel.setText("R$ 0,00");
        }
    }

    public static class ResultadoPagamento {
        private final String formaPagamento;
        private final BigDecimal valorPago;

        public ResultadoPagamento(String formaPagamento, BigDecimal valorPago) {
            this.formaPagamento = formaPagamento;
            this.valorPago = valorPago;
        }

        public String getFormaPagamento() {
            return formaPagamento;
        }

        public BigDecimal getValorPago() {
            return valorPago;
        }
    }
}
