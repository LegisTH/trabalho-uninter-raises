
package com.rede.rede_restaurantes.controller;

import com.rede.rede_restaurantes.dto.CriarPedido;
import com.rede.rede_restaurantes.entity.Pedido;
import com.rede.rede_restaurantes.service.PedidoService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public List<Pedido> listarTodos(
            @RequestParam(required = false) String canalPedido) {

        if (canalPedido != null && !canalPedido.isBlank()) {
            return pedidoService.listarPorCanal(canalPedido);
        }

        return pedidoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Pedido> listarPorUsuario(@PathVariable Long usuarioId) {
        return pedidoService.listarPorUsuario(usuarioId);
    }

    @GetMapping("/unidade/{unidadeId}")
    public List<Pedido> listarPorUnidade(@PathVariable Long unidadeId) {
        return pedidoService.listarPorUnidade(unidadeId);
    }

    @GetMapping("/status/{status}")
    public List<Pedido> listarPorStatus(@PathVariable String status) {
        return pedidoService.listarPorStatus(status);
    }

    @PostMapping
    public Pedido criar(@Valid @RequestBody CriarPedido pedido) {
        return pedidoService.salvar(pedido);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Pedido> atualizar(
            @PathVariable Long id,
            @RequestBody Pedido pedido) {

        return ResponseEntity.ok(pedidoService.atualizar(id, pedido));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        pedidoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
