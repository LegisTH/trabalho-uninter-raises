
package com.rede.rede_restaurantes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "estoque",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"produto_unidade_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
public class Estoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O produto da unidade é obrigatório")
    @Column(name = "produto_unidade_id", nullable = false)
    private Long produtoUnidadeId;

    @NotNull(message = "A quantidade atual é obrigatória")
    @PositiveOrZero(message = "A quantidade atual não pode ser negativa")
    @Column(name = "quantidade_atual", nullable = false)
    private Integer quantidadeAtual;

    @NotNull(message = "O estoque mínimo é obrigatório")
    @PositiveOrZero(message = "O estoque mínimo não pode ser negativo")
    @Column(name = "estoque_minimo", nullable = false)
    private Integer estoqueMinimo;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    @PreUpdate
    public void atualizarData() {
        atualizadoEm = LocalDateTime.now();
    }
}
