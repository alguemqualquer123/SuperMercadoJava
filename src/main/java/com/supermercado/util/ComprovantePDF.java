package com.supermercado.util;

import com.supermercado.model.Venda;
import com.supermercado.model.ItemVenda;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/**
 * Utilitário para geração de comprovantes em PDF
 */
public class ComprovantePDF {

    private static final Logger logger = LoggerFactory.getLogger(ComprovantePDF.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final String DIRETORIO_COMPROVANTES = "comprovantes";

    static {
        // Cria diretório de comprovantes se não existir
        File dir = new File(DIRETORIO_COMPROVANTES);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Gera comprovante em PDF
     */
    public static File gerarComprovante(Venda venda) {
        logger.debug("Gerando comprovante para venda ID {}", venda.getId());

        try {
            String nomeArquivo = String.format("%s/venda_%d_%s.pdf",
                    DIRETORIO_COMPROVANTES,
                    venda.getId(),
                    System.currentTimeMillis());

            File arquivo = new File(nomeArquivo);

            PdfWriter writer = new PdfWriter(new FileOutputStream(arquivo));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Cabeçalho
            document.add(new Paragraph("SUPERMERCADO PDV")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(18)
                    .setBold());

            document.add(new Paragraph("Comprovante de Venda")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14)
                    .setBold()
                    .setMarginBottom(10));

            // Informações da venda
            document.add(new Paragraph("Venda #" + venda.getId())
                    .setFontSize(10));
            document.add(new Paragraph("Data: " + venda.getDataVenda().format(DATE_FORMATTER))
                    .setFontSize(10));
            document.add(new Paragraph("Atendente: " + venda.getUsuario().getNome())
                    .setFontSize(10)
                    .setMarginBottom(10));

            // Tabela de itens
            float[] columnWidths = { 4, 1, 2, 2 };
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            // Cabeçalho da tabela
            table.addHeaderCell(new Cell().add(new Paragraph("Produto")).setBold());
            table.addHeaderCell(new Cell().add(new Paragraph("Qtd")).setBold()
                    .setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Preço Unit.")).setBold()
                    .setTextAlignment(TextAlignment.RIGHT));
            table.addHeaderCell(new Cell().add(new Paragraph("Subtotal")).setBold()
                    .setTextAlignment(TextAlignment.RIGHT));

            // Itens
            for (ItemVenda item : venda.getItens()) {
                table.addCell(new Cell().add(new Paragraph(item.getNomeProduto())
                        .setFontSize(9)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantidade()))
                        .setFontSize(9))
                        .setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(formatarMoeda(item.getPrecoUnitario()))
                        .setFontSize(9))
                        .setTextAlignment(TextAlignment.RIGHT));
                table.addCell(new Cell().add(new Paragraph(formatarMoeda(item.getSubtotal()))
                        .setFontSize(9))
                        .setTextAlignment(TextAlignment.RIGHT));
            }

            document.add(table);

            // Totais
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Subtotal: " + formatarMoeda(venda.getSubtotal()))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(10));

            if (venda.getDesconto().compareTo(BigDecimal.ZERO) > 0) {
                document.add(new Paragraph("Desconto: -" + formatarMoeda(venda.getDesconto()))
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setFontSize(10));
            }

            document.add(new Paragraph("TOTAL: " + formatarMoeda(venda.getTotal()))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(14)
                    .setBold());

            // Pagamento
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Forma de Pagamento: " + venda.getFormaPagamento())
                    .setFontSize(10));
            document.add(new Paragraph("Valor Pago: " + formatarMoeda(venda.getValorPago()))
                    .setFontSize(10));

            if (venda.getTroco().compareTo(BigDecimal.ZERO) > 0) {
                document.add(new Paragraph("Troco: " + formatarMoeda(venda.getTroco()))
                        .setFontSize(10));
            }

            // Rodapé
            document.add(new Paragraph("\n\nObrigado pela preferência!")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(10)
                    .setItalic());

            document.close();

            logger.info("Comprovante gerado: {}", arquivo.getAbsolutePath());
            return arquivo;

        } catch (Exception e) {
            logger.error("Erro ao gerar comprovante", e);
            throw new RuntimeException("Erro ao gerar comprovante: " + e.getMessage(), e);
        }
    }

    /**
     * Formata valor monetário
     */
    private static String formatarMoeda(BigDecimal valor) {
        return String.format("R$ %.2f", valor);
    }
}
