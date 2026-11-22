package com.rede.rede_restaurantes.repository;

import com.rede.rede_restaurantes.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByNomeContainingIgnoreCase(String nome);

    boolean existsByNome(String nome);
}