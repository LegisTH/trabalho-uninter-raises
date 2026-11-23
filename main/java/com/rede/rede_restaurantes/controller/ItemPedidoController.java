
package com.rede.rede_restaurantes.controller;

import com.rede.rede_restaurantes.entity.ItemPedido;
import com.rede.rede_restaurantes.service.ItemPedidoService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itens-pedido")
public class ItemPedidoController {

    private final ItemPedidoService itemPedidoService;

    public ItemPedidoController(ItemPedidoService itemPedidoService) {
        this.itemPedidoService = itemPedidoService;
    }

    @GetMapping
    public List<ItemPedido> listarTodos() {
        return itemPedidoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemPedido> buscarPorId(@PathVariable Long id) {
        return itemPedidoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{pedidoId}")
    public List<ItemPedido> listarPorPedido(@PathVariable Long pedidoId) {
        return itemPedidoService.listarPorPedido(pedidoId);
    }

    @GetMapping("/produto-unidade/{produtoUnidadeId}")
    public List<ItemPedido> listarPorProdutoUnidade(
            @PathVariable Long produtoUnidadeId) {

        return itemPedidoService.listarPorProdutoUnidade(produtoUnidadeId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ItemPedido criar(@RequestBody ItemPedido itemPedido) {
        return itemPedidoService.salvar(itemPedido);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ItemPedido> atualizar(
            @PathVariable Long id,
            @RequestBody ItemPedido itemPedido) {

        return itemPedidoService.buscarPorId(id)
                .map(itemExistente -> {
                    itemPedido.setId(id);
                    return ResponseEntity.ok(
                            itemPedidoService.salvar(itemPedido));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        if (itemPedidoService.buscarPorId(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        itemPedidoService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
