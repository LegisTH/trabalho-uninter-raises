package com.rede.rede_restaurantes.entity;

import jakarta.persistence.*;
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

    @Column(name = "carteira_id", nullable = false)
    private Long carteiraId;

    @Column(name = "pedido_id")
    private Long pedidoId;

    @Column(nullable = false)
    private String tipo;

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