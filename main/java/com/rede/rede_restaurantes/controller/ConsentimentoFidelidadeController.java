
package com.rede.rede_restaurantes.controller;

import com.rede.rede_restaurantes.entity.ConsentimentoFidelidade;
import com.rede.rede_restaurantes.service.ConsentimentoFidelidadeService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consentimentos-fidelidade")
public class ConsentimentoFidelidadeController {

    private final ConsentimentoFidelidadeService consentimentoFidelidadeService;

    public ConsentimentoFidelidadeController(
            ConsentimentoFidelidadeService consentimentoFidelidadeService) {

        this.consentimentoFidelidadeService = consentimentoFidelidadeService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ConsentimentoFidelidade> listarTodos() {
        return consentimentoFidelidadeService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsentimentoFidelidade> buscarPorId(
            @PathVariable Long id) {

        return consentimentoFidelidadeService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ConsentimentoFidelidade> buscarPorUsuario(
            @PathVariable Long usuarioId) {

        return consentimentoFidelidadeService.buscarPorUsuario(usuarioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ConsentimentoFidelidade criar(
            @RequestBody ConsentimentoFidelidade consentimento) {

        return consentimentoFidelidadeService.salvar(consentimento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsentimentoFidelidade> atualizar(
            @PathVariable Long id,
            @RequestBody ConsentimentoFidelidade consentimento) {

        return ResponseEntity.ok(
                consentimentoFidelidadeService.atualizar(id, consentimento));
    }
}
