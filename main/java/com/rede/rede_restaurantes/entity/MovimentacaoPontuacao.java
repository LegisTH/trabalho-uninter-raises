
package com.rede.rede_restaurantes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacao_pontuacao")
@Getter
@Setter
@NoArgsConstructor
public class MovimentacaoPontuacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A carteira é obrigatória")
    @Column(name = "carteira_id", nullable = false)
    private Long carteiraId;

    @Column(name = "pedido_id")
    private Long pedidoId;

    @NotBlank(message = "O tipo de movimentação é obrigatório")
    @Column(nullable = false)
    private String tipo;

    @NotNull(message = "A quantidade de pontos é obrigatória")
    @Positive(message = "A quantidade de pontos deve ser maior que zero")
    @Column(nullable = false)
    private Integer quantidade;

    private String descricao;

    @Column(name = "feito_em", nullable = false)
    private LocalDateTime feitoEm;

    @PrePersist
    public void prePersist() {
        if (feitoEm == null) {
            feitoEm = LocalDateTime.now();
        }
    }
}
