package com.rede.rede_restaurantes.repository;

import com.rede.rede_restaurantes.entity.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {

    Optional<Estoque> findByProdutoUnidadeId(Long produtoUnidadeId);
}