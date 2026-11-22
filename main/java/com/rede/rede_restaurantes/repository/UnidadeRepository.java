package com.rede.rede_restaurantes.repository;

import com.rede.rede_restaurantes.entity.Unidade;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnidadeRepository extends JpaRepository<Unidade, Long> {

    boolean existsByNome(String nome);
}