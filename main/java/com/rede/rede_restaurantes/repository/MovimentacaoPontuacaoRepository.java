package com.rede.rede_restaurantes.repository;

import com.rede.rede_restaurantes.entity.MovimentacaoPontuacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoPontuacaoRepository extends JpaRepository<MovimentacaoPontuacao, Long> {

    List<MovimentacaoPontuacao> findByCarteiraId(Long carteiraId);

    List<MovimentacaoPontuacao> findByPedidoId(Long pedidoId);
}