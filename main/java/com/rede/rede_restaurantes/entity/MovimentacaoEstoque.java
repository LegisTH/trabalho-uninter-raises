
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
@Table(name = "movimentacao_estoque")
@Getter
@Setter
@NoArgsConstructor
public class MovimentacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O estoque é obrigatório")
    @Column(name = "estoque_id", nullable = false)
    private Long estoqueId;

    @NotNull(message = "O usuário é obrigatório")
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @NotBlank(message = "O tipo de movimentação é obrigatório")
    @Column(nullable = false)
    private String tipo;

    @NotNull(message = "A quantidade é obrigatória")
    @Positive(message = "A quantidade deve ser maior que zero")
    @Column(nullable = false)
    private Integer quantidade;

    private String motivo;

    @Column(name = "feito_em", nullable = false)
    private LocalDateTime feitoEm;

    @PrePersist
    public void prePersist() {
        if (feitoEm == null) {
            feitoEm = LocalDateTime.now();
        }
    }
}
