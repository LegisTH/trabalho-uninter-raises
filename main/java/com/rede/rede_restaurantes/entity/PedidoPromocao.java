
package com.rede.rede_restaurantes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "pedido_promocao",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"pedido_id", "promocao_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
public class PedidoPromocao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O pedido é obrigatório")
    @Positive(message = "O ID do pedido deve ser maior que zero")
    @Column(name = "pedido_id", nullable = false)
    private Long pedidoId;

    @NotNull(message = "A promoção é obrigatória")
    @Positive(message = "O ID da promoção deve ser maior que zero")
    @Column(name = "promocao_id", nullable = false)
    private Long promocaoId;
}
