package com.rede.rede_restaurantes.controller;

import com.rede.rede_restaurantes.entity.ConsentimentoFidelidade;
import com.rede.rede_restaurantes.service.ConsentimentoFidelidadeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConsentimentoFidelidade> buscarPorId(@PathVariable Long id) {
        return consentimentoFidelidadeService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConsentimentoFidelidade> buscarPorUsuario(
            @PathVariable Long usuarioId) {
        return consentimentoFidelidadeService.buscarPorUsuario(usuarioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ConsentimentoFidelidade criar(
            @Valid @RequestBody ConsentimentoFidelidade consentimento) {
        return consentimentoFidelidadeService.salvar(consentimento);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConsentimentoFidelidade> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ConsentimentoFidelidade consentimento) {

        return consentimentoFidelidadeService.buscarPorId(id)
                .map(consentimentoExistente -> {
                    consentimento.setId(id);
                    return ResponseEntity.ok(
                            consentimentoFidelidadeService.salvar(consentimento));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        return consentimentoFidelidadeService.buscarPorId(id)
                .map(consentimentoExistente -> {
                    consentimentoFidelidadeService.excluir(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}