
package com.rede.rede_restaurantes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "carteira_pontuacao")
@Getter
@Setter
@NoArgsConstructor
public class CarteiraPontuacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O usuário é obrigatório")
    @Column(name = "usuario_id", nullable = false, unique = true)
    private Long usuarioId;

    @NotNull(message = "O saldo é obrigatório")
    @PositiveOrZero(message = "O saldo não pode ser negativo")
    @Column(nullable = false)
    private Integer saldo = 0;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    @PreUpdate
    public void atualizarData() {
        atualizadoEm = LocalDateTime.now();
    }
}
