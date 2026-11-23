package com.rede.rede_restaurantes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SolicitarPagamento {

    @NotNull(message = "O ID do pedido é obrigatório")
    @Positive(message = "O ID do pedido deve ser positivo")
    private Long pedidoId;

    @NotBlank(message = "O método de pagamento é obrigatório")
    private String metodoPagamento;

    @NotBlank(message = "A chave de idempotência é obrigatória")
    private String chaveIdempotencia;
}