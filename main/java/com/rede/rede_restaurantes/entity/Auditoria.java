
package com.rede.rede_restaurantes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
@Getter
@Setter
@NoArgsConstructor
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @NotBlank(message = "A ação da auditoria é obrigatória")
    @Column(nullable = false)
    private String acao;

    @Column(name = "entidade_afetada")
    private String entidadeAfetada;

    @Column(name = "registro_id")
    private Long registroId;

    @Column(name = "feito_em", nullable = false)
    private LocalDateTime feitoEm;

    @PrePersist
    public void prePersist() {
        if (feitoEm == null) {
            feitoEm = LocalDateTime.now();
        }
    }
}
