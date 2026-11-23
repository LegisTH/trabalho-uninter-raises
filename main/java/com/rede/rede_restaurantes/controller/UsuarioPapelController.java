
package com.rede.rede_restaurantes.controller;

import com.rede.rede_restaurantes.entity.UsuarioPapel;
import com.rede.rede_restaurantes.service.UsuarioPapelService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios-papeis")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioPapelController {

    private final UsuarioPapelService usuarioPapelService;

    public UsuarioPapelController(UsuarioPapelService usuarioPapelService) {
        this.usuarioPapelService = usuarioPapelService;
    }

    @GetMapping
    public List<UsuarioPapel> listarTodos() {
        return usuarioPapelService.listarTodos();
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<UsuarioPapel> buscarPorUsuario(
            @PathVariable Long usuarioId) {

        return usuarioPapelService.buscarPorUsuario(usuarioId);
    }

    @PostMapping("/usuario/{usuarioId}/papel/{papelId}")
    public UsuarioPapel associar(
            @PathVariable Long usuarioId,
            @PathVariable Long papelId) {

        return usuarioPapelService.associar(usuarioId, papelId);
    }

    @DeleteMapping("/usuario/{usuarioId}/papel/{papelId}")
    public ResponseEntity<Void> remover(
            @PathVariable Long usuarioId,
            @PathVariable Long papelId) {

        usuarioPapelService.remover(usuarioId, papelId);

        return ResponseEntity.noContent().build();
    }
}
