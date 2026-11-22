package com.rede.rede_restaurantes.controller;

import com.rede.rede_restaurantes.entity.PromocaoProduto;
import com.rede.rede_restaurantes.service.PromocaoProdutoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/promocoes-produtos")
public class PromocaoProdutoController {

    private final PromocaoProdutoService promocaoProdutoService;

    public PromocaoProdutoController(PromocaoProdutoService promocaoProdutoService) {
        this.promocaoProdutoService = promocaoProdutoService;
    }

    @GetMapping
    public List<PromocaoProduto> listarTodos() {
        return promocaoProdutoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromocaoProduto> buscarPorId(@PathVariable Long id) {
        return promocaoProdutoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/promocao/{promocaoId}")
    public List<PromocaoProduto> listarPorPromocao(@PathVariable Long promocaoId) {
        return promocaoProdutoService.listarPorPromocao(promocaoId);
    }

    @GetMapping("/produto/{produtoId}")
    public List<PromocaoProduto> listarPorProduto(@PathVariable Long produtoId) {
        return promocaoProdutoService.listarPorProduto(produtoId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PromocaoProduto criar(@Valid @RequestBody PromocaoProduto promocaoProduto) {
        return promocaoProdutoService.salvar(promocaoProduto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PromocaoProduto> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PromocaoProduto promocaoProduto) {

        return promocaoProdutoService.buscarPorId(id)
                .map(promocaoExistente -> {
                    promocaoProduto.setId(id);
                    return ResponseEntity.ok(promocaoProdutoService.salvar(promocaoProduto));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        return promocaoProdutoService.buscarPorId(id)
                .map(promocaoExistente -> {
                    promocaoProdutoService.excluir(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}