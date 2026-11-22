package com.rede.rede_restaurantes.repository;

import com.rede.rede_restaurantes.entity.PedidoPromocao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoPromocaoRepository extends JpaRepository<PedidoPromocao, Long> {

    List<PedidoPromocao> findByPedidoId(Long pedidoId);

    List<PedidoPromocao> findByPromocaoId(Long promocaoId);
}