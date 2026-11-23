
package com.rede.rede_restaurantes.controller;

import com.rede.rede_restaurantes.entity.Auditoria;
import com.rede.rede_restaurantes.service.AuditoriaService;

import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/auditorias")
@PreAuthorize("hasRole('ADMIN')")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public List<Auditoria> listarTodas() {
        return auditoriaService.listarTodas();
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Auditoria> listarPorUsuario(
            @PathVariable Long usuarioId) {

        return auditoriaService.listarPorUsuario(usuarioId);
    }

    @GetMapping("/entidade/{entidadeAfetada}")
    public List<Auditoria> listarPorEntidade(
            @PathVariable String entidadeAfetada) {

        return auditoriaService.listarPorEntidade(entidadeAfetada);
    }

    @PostMapping
    public Auditoria criar(@Valid @RequestBody Auditoria auditoria) {
        return auditoriaService.salvar(auditoria);
    }
}
