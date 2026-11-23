package com.rede.rede_restaurantes.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.rede.rede_restaurantes.dto.RespostaGateway;

@Service
public class GatewayPagamentoService {

    public RespostaGateway processarPagamento(String metodoPagamento, BigDecimal valor) {

        // Simulação de recusa para permitir testar os dois cenários.
        boolean aprovado = !metodoPagamento.equalsIgnoreCase("RECUSADO");

        String identificadorExterno = "PG-" + UUID.randomUUID();

        if (aprovado) {
            return new RespostaGateway(
                    true,
                    identificadorExterno,
                    "APROVADO",
                    "Pagamento aprovado pelo gateway simulado");
        }

        return new RespostaGateway(
                false,
                identificadorExterno,
                "RECUSADO",
                "Pagamento recusado pelo gateway simulado");
    }
}