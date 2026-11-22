package com.rede.rede_restaurantes.repository;

import com.rede.rede_restaurantes.entity.Papel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PapelRepository extends JpaRepository<Papel, Long> {

    Optional<Papel> findByNome(String nome);

    boolean existsByNome(String nome);
}