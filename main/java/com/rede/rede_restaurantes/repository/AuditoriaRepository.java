package com.rede.rede_restaurantes.repository;

import com.rede.rede_restaurantes.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    List<Auditoria> findByUsuarioId(Long usuarioId);

    List<Auditoria> findByEntidadeAfetada(String entidadeAfetada);
}