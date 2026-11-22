
package com.rede.rede_restaurantes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "consentimento_fidelidade")
@Getter
@Setter
@NoArgsConstructor
public class ConsentimentoFidelidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O usuário é obrigatório")
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @NotNull(message = "É necessário informar se o consentimento foi aceito")
    @Column(nullable = false)
    private Boolean aceito;

    @Column(name = "data_consentimento")
    private LocalDateTime dataConsentimento;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
        LocalDateTime agora = LocalDateTime.now();

        if (dataConsentimento == null) {
            dataConsentimento = agora;
        }

        atualizadoEm = agora;
    }

    @PreUpdate
    public void preUpdate() {
        atualizadoEm = LocalDateTime.now();
    }
}
