package com.rede.rede_restaurantes.entity;

import jakarta.persistence.*;
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

    @Column(name = "promocao_id", nullable = false)
    private Long promocaoId;

    @Column(name = "produto_id", nullable = false)
    private Long produtoId;
}