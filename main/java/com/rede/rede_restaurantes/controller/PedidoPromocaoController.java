package com.rede.rede_restaurantes.controller;

import com.rede.rede_restaurantes.entity.PedidoPromocao;
import com.rede.rede_restaurantes.service.PedidoPromocaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos-promocoes")
public class PedidoPromocaoController {

    private final PedidoPromocaoService pedidoPromocaoService;

    public PedidoPromocaoController(PedidoPromocaoService pedidoPromocaoService) {
        this.pedidoPromocaoService = pedidoPromocaoService;
    }

    @GetMapping
    public List<PedidoPromocao> listarTodos() {
        return pedidoPromocaoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoPromocao> buscarPorId(@PathVariable Long id) {
        return pedidoPromocaoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{pedidoId}")
    public List<PedidoPromocao> listarPorPedido(@PathVariable Long pedidoId) {
        return pedidoPromocaoService.listarPorPedido(pedidoId);
    }

    @GetMapping("/promocao/{promocaoId}")
    public List<PedidoPromocao> listarPorPromocao(@PathVariable Long promocaoId) {
        return pedidoPromocaoService.listarPorPromocao(promocaoId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PedidoPromocao criar(@Valid @RequestBody PedidoPromocao pedidoPromocao) {
        return pedidoPromocaoService.salvar(pedidoPromocao);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PedidoPromocao> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PedidoPromocao pedidoPromocao) {

        return pedidoPromocaoService.buscarPorId(id)
                .map(pedidoPromocaoExistente -> {
                    pedidoPromocao.setId(id);
                    return ResponseEntity.ok(pedidoPromocaoService.salvar(pedidoPromocao));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        return pedidoPromocaoService.buscarPorId(id)
                .map(pedidoPromocaoExistente -> {
                    pedidoPromocaoService.excluir(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}