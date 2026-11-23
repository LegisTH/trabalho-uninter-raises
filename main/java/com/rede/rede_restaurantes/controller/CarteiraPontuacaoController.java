
package com.rede.rede_restaurantes.controller;

import com.rede.rede_restaurantes.entity.CarteiraPontuacao;
import com.rede.rede_restaurantes.service.CarteiraPontuacaoService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carteiras-pontuacao")
public class CarteiraPontuacaoController {

    private final CarteiraPontuacaoService carteiraPontuacaoService;

    public CarteiraPontuacaoController(
            CarteiraPontuacaoService carteiraPontuacaoService) {

        this.carteiraPontuacaoService = carteiraPontuacaoService;
    }

    @GetMapping
    public List<CarteiraPontuacao> listarTodos() {
        return carteiraPontuacaoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarteiraPontuacao> buscarPorId(
            @PathVariable Long id) {

        return carteiraPontuacaoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<CarteiraPontuacao> buscarPorUsuario(
            @PathVariable Long usuarioId) {

        return carteiraPontuacaoService.buscarPorUsuario(usuarioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CarteiraPontuacao criar(
            @Valid @RequestBody CarteiraPontuacao carteira) {

        return carteiraPontuacaoService.salvar(carteira);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        carteiraPontuacaoService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
