
package com.rede.rede_restaurantes.controller;

import com.rede.rede_restaurantes.entity.MovimentacaoPontuacao;
import com.rede.rede_restaurantes.service.MovimentacaoPontuacaoService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimentacoes-pontuacao")
public class MovimentacaoPontuacaoController {

    private final MovimentacaoPontuacaoService movimentacaoPontuacaoService;

    public MovimentacaoPontuacaoController(
            MovimentacaoPontuacaoService movimentacaoPontuacaoService) {

        this.movimentacaoPontuacaoService = movimentacaoPontuacaoService;
    }

    @GetMapping
    public List<MovimentacaoPontuacao> listarTodos() {
        return movimentacaoPontuacaoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimentacaoPontuacao> buscarPorId(
            @PathVariable Long id) {

        return movimentacaoPontuacaoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/carteira/{carteiraId}")
    public List<MovimentacaoPontuacao> listarPorCarteira(
            @PathVariable Long carteiraId) {

        return movimentacaoPontuacaoService.listarPorCarteira(carteiraId);
    }

    @GetMapping("/pedido/{pedidoId}")
    public List<MovimentacaoPontuacao> listarPorPedido(
            @PathVariable Long pedidoId) {

        return movimentacaoPontuacaoService.listarPorPedido(pedidoId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public MovimentacaoPontuacao criar(
            @Valid @RequestBody MovimentacaoPontuacao movimentacao) {

        return movimentacaoPontuacaoService.salvar(movimentacao);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        movimentacaoPontuacaoService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
