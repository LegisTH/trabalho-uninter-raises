
package com.rede.rede_restaurantes.repository;

import com.rede.rede_restaurantes.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUsuarioId(Long usuarioId);

    List<Pedido> findByUnidadeId(Long unidadeId);

    List<Pedido> findByStatus(String status);

    List<Pedido> findByCanalPedido(String canalPedido);
}
