
package com.rede.rede_restaurantes.controller;

import com.rede.rede_restaurantes.entity.Unidade;
import com.rede.rede_restaurantes.service.UnidadeService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/unidades")
public class UnidadeController {

    private final UnidadeService unidadeService;

    public UnidadeController(UnidadeService unidadeService) {
        this.unidadeService = unidadeService;
    }

    @GetMapping
    public List<Unidade> listarTodas() {
        return unidadeService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Unidade> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(unidadeService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Unidade criar(@Valid @RequestBody Unidade unidade) {
        return unidadeService.salvar(unidade);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Unidade> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Unidade unidade) {

        Unidade unidadeExistente = unidadeService.buscarPorId(id);

        unidade.setId(unidadeExistente.getId());

        return ResponseEntity.ok(unidadeService.salvar(unidade));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        unidadeService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
