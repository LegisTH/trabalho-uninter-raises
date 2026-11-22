
package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.ItemPedido;
import com.rede.rede_restaurantes.entity.Pedido;
import com.rede.rede_restaurantes.entity.Usuario;
import com.rede.rede_restaurantes.repository.ItemPedidoRepository;
import com.rede.rede_restaurantes.repository.PedidoRepository;
import com.rede.rede_restaurantes.repository.ProdutoUnidadeRepository;
import com.rede.rede_restaurantes.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemPedidoService {

    private final ItemPedidoRepository itemPedidoRepository;
    private final PedidoRepository pedidoRepository;
    private final ProdutoUnidadeRepository produtoUnidadeRepository;
    private final UsuarioRepository usuarioRepository;

    public ItemPedidoService(
            ItemPedidoRepository itemPedidoRepository,
            PedidoRepository pedidoRepository,
            ProdutoUnidadeRepository produtoUnidadeRepository,
            UsuarioRepository usuarioRepository) {

        this.itemPedidoRepository = itemPedidoRepository;
        this.pedidoRepository = pedidoRepository;
        this.produtoUnidadeRepository = produtoUnidadeRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private boolean usuarioEhAdmin() {
        Authentication autenticacao =
                SecurityContextHolder.getContext().getAuthentication();

        return autenticacao != null
                && autenticacao.getAuthorities().stream()
                .anyMatch(autoridade ->
                        autoridade.getAuthority().equals("ROLE_ADMIN"));
    }

    private Usuario usuarioAtual() {
        Authentication autenticacao =
                SecurityContextHolder.getContext().getAuthentication();

        if (autenticacao == null || !autenticacao.isAuthenticated()) {
            throw new AccessDeniedException("Usuário não autenticado");
        }

        return usuarioRepository.findByEmail(autenticacao.getName())
                .orElseThrow(() ->
                        new EntityNotFoundException("Usuário não encontrado"));
    }

    private Pedido verificarAcessoAoPedido(Long pedidoId) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Pedido não encontrado"));

        if (!usuarioEhAdmin()
                && !pedido.getUsuarioId().equals(usuarioAtual().getId())) {
            throw new AccessDeniedException(
                    "Você não tem permissão para acessar este pedido");
        }

        return pedido;
    }

    public List<ItemPedido> listarTodos() {

        if (usuarioEhAdmin()) {
            return itemPedidoRepository.findAll();
        }

        Long usuarioId = usuarioAtual().getId();

        return itemPedidoRepository.findAll().stream()
                .filter(item -> {
                    Optional<Pedido> pedido =
                            pedidoRepository.findById(item.getPedidoId());

                    return pedido.isPresent()
                            && pedido.get().getUsuarioId().equals(usuarioId);
                })
                .toList();
    }

    public Optional<ItemPedido> buscarPorId(Long id) {

        Optional<ItemPedido> item = itemPedidoRepository.findById(id);

        if (item.isEmpty() || usuarioEhAdmin()) {
            return item;
        }

        verificarAcessoAoPedido(item.get().getPedidoId());

        return item;
    }

    public List<ItemPedido> listarPorPedido(Long pedidoId) {

        verificarAcessoAoPedido(pedidoId);

        return itemPedidoRepository.findByPedidoId(pedidoId);
    }

    public List<ItemPedido> listarPorProdutoUnidade(Long produtoUnidadeId) {

        List<ItemPedido> itens =
                itemPedidoRepository.findByProdutoUnidadeId(produtoUnidadeId);

        if (usuarioEhAdmin()) {
            return itens;
        }

        Long usuarioId = usuarioAtual().getId();

        return itens.stream()
                .filter(item -> {
                    Optional<Pedido> pedido =
                            pedidoRepository.findById(item.getPedidoId());

                    return pedido.isPresent()
                            && pedido.get().getUsuarioId().equals(usuarioId);
                })
                .toList();
    }

    public ItemPedido salvar(ItemPedido itemPedido) {

        if (itemPedido.getPedidoId() == null) {
            throw new EntityNotFoundException("Pedido não encontrado");
        }

        verificarAcessoAoPedido(itemPedido.getPedidoId());

        if (itemPedido.getProdutoUnidadeId() == null
                || !produtoUnidadeRepository.existsById(
                        itemPedido.getProdutoUnidadeId())) {
            throw new EntityNotFoundException(
                    "Produto da unidade não encontrado");
        }

        return itemPedidoRepository.save(itemPedido);
    }

    public void excluir(Long id) {
        itemPedidoRepository.deleteById(id);
    }
}
