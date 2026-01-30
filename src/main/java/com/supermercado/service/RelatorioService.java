package com.supermercado.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.supermercado.model.Venda;
import com.supermercado.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RelatorioService {

    @Autowired
    private VendaRepository vendaRepository;

    public void gerarRelatorioFinanceiro(LocalDateTime inicio, LocalDateTime fim, String destPath) throws Exception {
        List<Venda> vendas = vendaRepository.findByDataVendaBetweenOrderByDataVendaDesc(inicio, fim);
        BigDecimal total = vendaRepository.calcularTotalPeriodo(inicio, fim);

        PdfWriter writer = new PdfWriter(destPath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Relatório Financeiro de Vendas").setFontSize(20).setBold());
        document.add(new Paragraph("Período: " + inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " a " + fim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        document.add(new Paragraph("\n"));

        float[] columnWidths = { 1, 3, 2, 2 };
        Table table = new Table(columnWidths);
        table.addHeaderCell("ID");
        table.addHeaderCell("Data");
        table.addHeaderCell("Status");
        table.addHeaderCell("Total");

        for (Venda venda : vendas) {
            table.addCell(venda.getId().toString());
            table.addCell(venda.getDataVenda().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            table.addCell(venda.getStatus().toString());
            table.addCell("R$ " + venda.getTotal().toString());
        }

        document.add(table);
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Total do Período: R$ " + total.toString()).setBold().setFontSize(16));

        document.close();
    }
}
