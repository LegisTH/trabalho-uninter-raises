package com.rede.rede_restaurantes.repository;

import com.rede.rede_restaurantes.entity.CarteiraPontuacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarteiraPontuacaoRepository extends JpaRepository<CarteiraPontuacao, Long> {

    Optional<CarteiraPontuacao> findByUsuarioId(Long usuarioId);
}