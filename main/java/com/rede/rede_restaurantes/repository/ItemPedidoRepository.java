package com.rede.rede_restaurantes.repository;

import com.rede.rede_restaurantes.entity.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

    List<ItemPedido> findByPedidoId(Long pedidoId);

    List<ItemPedido> findByProdutoUnidadeId(Long produtoUnidadeId);
}