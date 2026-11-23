
package com.rede.rede_restaurantes.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

public record CriarPedido(

        @NotNull(message = "A unidade é obrigatória")
        Long unidadeId,

        @NotBlank(message = "O canal do pedido é obrigatório")
        String canalPedido,

        @NotEmpty(message = "O pedido deve possuir pelo menos um item")
        List<@Valid Item> itens,

        String observacao

) {

    public record Item(

            @NotNull(message = "O produto da unidade é obrigatório")
            Long produtoUnidadeId,

            @NotNull(message = "A quantidade é obrigatória")
            @Positive(message = "A quantidade deve ser maior que zero")
            Integer quantidade

    ) {
    }
}
