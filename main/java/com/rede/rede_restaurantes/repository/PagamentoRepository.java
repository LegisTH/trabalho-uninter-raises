package com.rede.rede_restaurantes.repository;

import com.rede.rede_restaurantes.entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    List<Pagamento> findByPedidoId(Long pedidoId);

    Optional<Pagamento> findByIdentificadorExterno(String identificadorExterno);

    Optional<Pagamento> findByChaveIdempotencia(String chaveIdempotencia);

    List<Pagamento> findByStatus(String status);

    boolean existsByPedidoIdAndStatusIn(
            Long pedidoId,
            List<String> status
    );

    boolean existsByPedidoIdAndStatusInAndIdNot(
            Long pedidoId,
            List<String> status,
            Long id
    );
}