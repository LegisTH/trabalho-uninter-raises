package com.rede.rede_restaurantes.repository;

import com.rede.rede_restaurantes.entity.ConsentimentoFidelidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConsentimentoFidelidadeRepository extends JpaRepository<ConsentimentoFidelidade, Long> {

    Optional<ConsentimentoFidelidade> findByUsuarioId(Long usuarioId);
}