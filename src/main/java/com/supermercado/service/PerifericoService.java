package com.supermercado.service;

import org.springframework.stereotype.Service;
import java.io.OutputStream;
import java.net.Socket;

@Service
public class PerifericoService {

    /**
     * Exemplo de impressão ESC/POS via rede (IP da Impressora Térmica)
     */
    public void imprimirComprovanteTermico(String texto, String ipImpressora, int porta) {
        try (Socket socket = new Socket(ipImpressora, porta);
                OutputStream out = socket.getOutputStream()) {

            // Comandos ESC/POS básicos
            byte[] init = { 0x1B, 0x40 }; // Inicializar
            byte[] cut = { 0x1D, 0x56, 0x41, 0x10 }; // Cortar papel

            out.write(init);
            out.write(texto.getBytes("CP850")); // Encode comum em impressoras térmicas
            out.write("\n\n\n".getBytes());
            out.write(cut);
            out.flush();

        } catch (Exception e) {
            System.err.println("Erro ao imprimir em periférico: " + e.getMessage());
        }
    }

    /**
     * Exemplo de leitura de balança (Simulado)
     */
    public double lerPesoBalanca(String portaCom) {
        // Implementação real usaria jSerialComm para ler da porta serial
        // Aqui apenas simulamos a integração
        return 1.450; // Retorna 1.45kg
    }
}
