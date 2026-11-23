
package com.rede.rede_restaurantes.controller;

import com.rede.rede_restaurantes.entity.Promocao;
import com.rede.rede_restaurantes.service.PromocaoService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/promocoes")
public class PromocaoController {

    private final PromocaoService promocaoService;

    public PromocaoController(PromocaoService promocaoService) {
        this.promocaoService = promocaoService;
    }

    @GetMapping
    public List<Promocao> listarTodas() {
        return promocaoService.listarTodas();
    }

    @GetMapping("/ativas")
    public List<Promocao> listarAtivas() {
        return promocaoService.listarAtivas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Promocao> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(promocaoService.buscarPorId(id));
    }

    @GetMapping("/buscar")
    public List<Promocao> buscarPorNome(@RequestParam String nome) {
        return promocaoService.buscarPorNome(nome);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Promocao criar(@Valid @RequestBody Promocao promocao) {
        promocao.setId(null);
        return promocaoService.salvar(promocao);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Promocao> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Promocao promocao) {

        promocao.setId(id);

        return ResponseEntity.ok(promocaoService.salvar(promocao));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        promocaoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
