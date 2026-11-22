package com.rede.rede_restaurantes.repository;

import com.rede.rede_restaurantes.entity.MovimentacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    List<MovimentacaoEstoque> findByEstoqueId(Long estoqueId);

    List<MovimentacaoEstoque> findByUsuarioId(Long usuarioId);
}