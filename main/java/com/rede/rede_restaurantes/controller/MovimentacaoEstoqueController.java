package com.rede.rede_restaurantes.controller;

import com.rede.rede_restaurantes.entity.MovimentacaoEstoque;
import com.rede.rede_restaurantes.service.MovimentacaoEstoqueService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/movimentacoes-estoque")
public class MovimentacaoEstoqueController {

    private final MovimentacaoEstoqueService movimentacaoEstoqueService;

    public MovimentacaoEstoqueController(
            MovimentacaoEstoqueService movimentacaoEstoqueService) {
        this.movimentacaoEstoqueService = movimentacaoEstoqueService;
    }

    @GetMapping
    public List<MovimentacaoEstoque> listarTodos() {
        return movimentacaoEstoqueService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimentacaoEstoque> buscarPorId(@PathVariable Long id) {
        return movimentacaoEstoqueService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/estoque/{estoqueId}")
    public List<MovimentacaoEstoque> listarPorEstoque(@PathVariable Long estoqueId) {
        return movimentacaoEstoqueService.listarPorEstoque(estoqueId);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<MovimentacaoEstoque> listarPorUsuario(@PathVariable Long usuarioId) {
        return movimentacaoEstoqueService.listarPorUsuario(usuarioId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public MovimentacaoEstoque criar(@Valid @RequestBody MovimentacaoEstoque movimentacao) {
        return movimentacaoEstoqueService.salvar(movimentacao);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovimentacaoEstoque> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody MovimentacaoEstoque movimentacao) {

        return movimentacaoEstoqueService.buscarPorId(id)
                .map(movimentacaoExistente -> {
                    movimentacao.setId(id);
                    return ResponseEntity.ok(movimentacaoEstoqueService.salvar(movimentacao));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        return movimentacaoEstoqueService.buscarPorId(id)
                .map(movimentacaoExistente -> {
                    movimentacaoEstoqueService.excluir(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}