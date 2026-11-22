package com.rede.rede_restaurantes.controller;

import com.rede.rede_restaurantes.entity.Estoque;
import com.rede.rede_restaurantes.service.EstoqueService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/estoques")
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @GetMapping
    public List<Estoque> listarTodos() {
        return estoqueService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estoque> buscarPorId(@PathVariable Long id) {
        return estoqueService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/produto-unidade/{produtoUnidadeId}")
    public ResponseEntity<Estoque> buscarPorProdutoUnidade(
            @PathVariable Long produtoUnidadeId) {
        return estoqueService.buscarPorProdutoUnidade(produtoUnidadeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Estoque criar(@Valid @RequestBody Estoque estoque) {
        return estoqueService.salvar(estoque);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Estoque> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Estoque estoque) {

        return estoqueService.buscarPorId(id)
                .map(estoqueExistente -> {
                    estoque.setId(id);
                    return ResponseEntity.ok(estoqueService.salvar(estoque));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        return estoqueService.buscarPorId(id)
                .map(estoqueExistente -> {
                    estoqueService.excluir(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}