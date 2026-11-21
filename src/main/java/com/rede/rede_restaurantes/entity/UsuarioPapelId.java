
package com.rede.rede_restaurantes.entity;

import java.io.Serializable;
import java.util.Objects;

public class UsuarioPapelId implements Serializable {

    private Long usuario;
    private Long papel;

    public UsuarioPapelId() {
    }

    public UsuarioPapelId(Long usuario, Long papel) {
        this.usuario = usuario;
        this.papel = papel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsuarioPapelId)) return false;

        UsuarioPapelId that = (UsuarioPapelId) o;

        return Objects.equals(usuario, that.usuario)
                && Objects.equals(papel, that.papel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usuario, papel);
    }
}
