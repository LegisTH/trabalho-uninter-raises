package com.rede.rede_restaurantes.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RespostaGateway {

    private boolean aprovado;
    private String identificadorExterno;
    private String status;
    private String mensagem;
}