
package com.rede.rede_restaurantes.repository;

import com.rede.rede_restaurantes.entity.UsuarioPapel;
import com.rede.rede_restaurantes.entity.UsuarioPapelId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;

public interface UsuarioPapelRepository
        extends JpaRepository<UsuarioPapel, UsuarioPapelId> {

    @EntityGraph(attributePaths = "papel")
    List<UsuarioPapel> findByUsuarioEmail(String email);
}
