
package com.rede.rede_restaurantes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "promocao_produto",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"promocao_id", "produto_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
public class PromocaoProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A promoção é obrigatória")
    @Column(name = "promocao_id", nullable = false)
    private Long promocaoId;

    @NotNull(message = "O produto é obrigatório")
    @Column(name = "produto_id", nullable = false)
    private Long produtoId;
}
