
package com.rede.rede_restaurantes.controller;

import com.rede.rede_restaurantes.entity.Pagamento;
import com.rede.rede_restaurantes.service.PagamentoService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @GetMapping
    public List<Pagamento> listarTodos() {
        return pagamentoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pagamento> buscarPorId(@PathVariable Long id) {
        return pagamentoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pedido/{pedidoId}")
    public List<Pagamento> listarPorPedido(@PathVariable Long pedidoId) {
        return pagamentoService.listarPorPedido(pedidoId);
    }

    @GetMapping("/identificador/{identificador}")
    public ResponseEntity<Pagamento> buscarPorIdentificadorExterno(
            @PathVariable String identificador) {

        return pagamentoService.buscarPorIdentificadorExterno(identificador)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public List<Pagamento> listarPorStatus(@PathVariable String status) {
        return pagamentoService.listarPorStatus(status);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Pagamento criar(@Valid @RequestBody Pagamento pagamento) {
        return pagamentoService.salvar(pagamento);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Pagamento> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Pagamento pagamento) {

        return ResponseEntity.ok(pagamentoService.atualizar(id, pagamento));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        pagamentoService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}
