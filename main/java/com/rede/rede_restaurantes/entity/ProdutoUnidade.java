
package com.rede.rede_restaurantes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(
    name = "produto_unidade",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"produto_id", "unidade_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
public class ProdutoUnidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O produto é obrigatório")
    @Column(name = "produto_id", nullable = false)
    private Long produtoId;

    @NotNull(message = "A unidade é obrigatória")
    @Column(name = "unidade_id", nullable = false)
    private Long unidadeId;

    @NotNull(message = "O preço é obrigatório")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(nullable = false)
    private Boolean disponivel = true;
}
