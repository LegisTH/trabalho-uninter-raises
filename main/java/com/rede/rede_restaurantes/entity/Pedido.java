
package com.rede.rede_restaurantes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedido")
@Getter
@Setter
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O usuário é obrigatório")
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @NotNull(message = "A unidade é obrigatória")
    @Column(name = "unidade_id", nullable = false)
    private Long unidadeId;

    @NotBlank(message = "O canal do pedido é obrigatório")
    @Column(name = "canal_pedido", nullable = false)
    private String canalPedido;

    @NotNull(message = "O subtotal é obrigatório")
    @DecimalMin(value = "0.00", message = "O subtotal não pode ser negativo")
    @Column(name = "valor_subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorSubtotal;

    @NotNull(message = "O desconto é obrigatório")
    @DecimalMin(value = "0.00", message = "O desconto não pode ser negativo")
    @Column(name = "valor_desconto", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDesconto;

    @NotNull(message = "O valor total é obrigatório")
    @DecimalMin(value = "0.00", message = "O valor total não pode ser negativo")
    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    private String observacao;

    @Column(name = "feito_em", nullable = false)
    private LocalDateTime feitoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @NotBlank(message = "O status do pedido é obrigatório")
    @Column(nullable = false)
    private String status;

    @PrePersist
    public void prePersist() {
        LocalDateTime agora = LocalDateTime.now();

        if (feitoEm == null) {
            feitoEm = agora;
        }

        atualizadoEm = agora;
    }

    @PreUpdate
    public void preUpdate() {
        atualizadoEm = LocalDateTime.now();
    }
}
