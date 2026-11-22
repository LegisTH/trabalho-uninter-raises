package com.rede.rede_restaurantes.controller;

import com.rede.rede_restaurantes.entity.ProdutoUnidade;
import com.rede.rede_restaurantes.service.ProdutoUnidadeService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/produtos-unidades")
public class ProdutoUnidadeController {

    private final ProdutoUnidadeService produtoUnidadeService;

    public ProdutoUnidadeController(ProdutoUnidadeService produtoUnidadeService) {
        this.produtoUnidadeService = produtoUnidadeService;
    }

    @GetMapping
    public List<ProdutoUnidade> listarTodas() {
        return produtoUnidadeService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoUnidade> buscarPorId(@PathVariable Long id) {
        return produtoUnidadeService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/unidade/{unidadeId}")
    public List<ProdutoUnidade> listarPorUnidade(@PathVariable Long unidadeId) {
        return produtoUnidadeService.listarPorUnidade(unidadeId);
    }

    @GetMapping("/produto/{produtoId}/unidade/{unidadeId}")
    public ResponseEntity<ProdutoUnidade> buscarPorProdutoEUnidade(
            @PathVariable Long produtoId,
            @PathVariable Long unidadeId) {

        return produtoUnidadeService.buscarPorProdutoEUnidade(produtoId, unidadeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProdutoUnidade criar(@Valid @RequestBody ProdutoUnidade produtoUnidade) {
        return produtoUnidadeService.salvar(produtoUnidade);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutoUnidade> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoUnidade produtoUnidade) {

        return produtoUnidadeService.buscarPorId(id)
                .map(produtoExistente -> {
                    produtoUnidade.setId(id);
                    return ResponseEntity.ok(produtoUnidadeService.salvar(produtoUnidade));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        return produtoUnidadeService.buscarPorId(id)
                .map(produtoExistente -> {
                    produtoUnidadeService.excluir(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}