package com.rede.rede_restaurantes.repository;

import com.rede.rede_restaurantes.entity.ProdutoUnidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ProdutoUnidadeRepository extends JpaRepository<ProdutoUnidade, Long> {

    Optional<ProdutoUnidade> findByProdutoIdAndUnidadeId(Long produtoId, Long unidadeId);

    List<ProdutoUnidade> findByUnidadeId(Long unidadeId);
}