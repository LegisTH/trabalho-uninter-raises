package com.rede.rede_restaurantes.repository;

import com.rede.rede_restaurantes.entity.PromocaoProduto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromocaoProdutoRepository extends JpaRepository<PromocaoProduto, Long> {

    List<PromocaoProduto> findByPromocaoId(Long promocaoId);

    List<PromocaoProduto> findByProdutoId(Long produtoId);
}