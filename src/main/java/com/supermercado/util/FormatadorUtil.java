package com.supermercado.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Utilitário para formatação de valores
 */
public class FormatadorUtil {

    private static final DecimalFormat DECIMAL_FORMAT;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("pt", "BR"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DECIMAL_FORMAT = new DecimalFormat("#,##0.00", symbols);
    }

    /**
     * Formata BigDecimal como moeda brasileira
     */
    public static String formatarMoeda(BigDecimal valor) {
        if (valor == null) {
            return "R$ 0,00";
        }
        return "R$ " + DECIMAL_FORMAT.format(valor);
    }

    /**
     * Formata data
     */
    public static String formatarData(LocalDate data) {
        if (data == null) {
            return "";
        }
        return data.format(DATE_FORMAT);
    }

    /**
     * Formata data e hora
     */
    public static String formatarDateTime(LocalDateTime dataHora) {
        if (dataHora == null) {
            return "";
        }
        return dataHora.format(DATE_TIME_FORMAT);
    }

    /**
     * Converte String para BigDecimal (aceita vírgula como decimal)
     */
    public static BigDecimal parseMoeda(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        // Remove "R$" e espaços
        valor = valor.replace("R$", "").trim();

        // Substitui vírgula por ponto
        valor = valor.replace(",", ".");

        // Remove pontos de milhar
        valor = valor.replaceAll("[^\\d.]", "");

        try {
            return new BigDecimal(valor);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
