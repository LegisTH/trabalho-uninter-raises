package com.rede.rede_restaurantes.repository;

import com.rede.rede_restaurantes.entity.Promocao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromocaoRepository extends JpaRepository<Promocao, Long> {

    List<Promocao> findByAtivoTrue();

    List<Promocao> findByNomeContainingIgnoreCase(String nome);
}