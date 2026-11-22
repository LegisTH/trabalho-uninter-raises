package com.rede.rede_restaurantes.controller;

import com.rede.rede_restaurantes.entity.Papel;
import com.rede.rede_restaurantes.service.PapelService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/papeis")
@PreAuthorize("hasRole('ADMIN')")
public class PapelController {

    private final PapelService papelService;

    
    public PapelController(PapelService papelService) {
        this.papelService = papelService;
    }

    @GetMapping
    public List<Papel> listarTodos() {
        return papelService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Papel> buscarPorId(@PathVariable Long id) {
        return papelService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Papel criar(@RequestBody Papel papel) {
        return papelService.salvar(papel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Papel> atualizar(
            @PathVariable Long id,
            @RequestBody Papel papel) {

        return papelService.buscarPorId(id)
                .map(papelExistente -> {
                    papel.setId(id);
                    return ResponseEntity.ok(papelService.salvar(papel));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        return papelService.buscarPorId(id)
                .map(papelExistente -> {
                    papelService.excluir(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}