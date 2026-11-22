
package com.rede.rede_restaurantes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamento")
@Getter
@Setter
@NoArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O pedido é obrigatório")
    @Column(name = "pedido_id", nullable = false)
    private Long pedidoId;

    @Column(name = "identificador_externo")
    private String identificadorExterno;

    @NotBlank(message = "O método de pagamento é obrigatório")
    @Column(name = "metodo_pagamento", nullable = false)
    private String metodoPagamento;

    @NotBlank(message = "O status do pagamento é obrigatório")
    @Column(nullable = false)
    private String status;

    @NotNull(message = "O valor do pagamento é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor do pagamento deve ser maior que zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload_enviado", columnDefinition = "jsonb")
    private String payloadEnviado;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload_retorno", columnDefinition = "jsonb")
    private String payloadRetorno;

    @Column(name = "feito_em")
    private LocalDateTime feitoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
        if (feitoEm == null) {
            feitoEm = LocalDateTime.now();
        }
        atualizadoEm = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        atualizadoEm = LocalDateTime.now();
    }
}
