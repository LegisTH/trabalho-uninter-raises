
package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.Estoque;
import com.rede.rede_restaurantes.entity.ItemPedido;
import com.rede.rede_restaurantes.entity.Pedido;
import com.rede.rede_restaurantes.entity.PedidoPromocao;
import com.rede.rede_restaurantes.entity.ProdutoUnidade;
import com.rede.rede_restaurantes.entity.Promocao;
import com.rede.rede_restaurantes.entity.Usuario;

import com.rede.rede_restaurantes.repository.EstoqueRepository;
import com.rede.rede_restaurantes.repository.ItemPedidoRepository;
import com.rede.rede_restaurantes.repository.PedidoRepository;
import com.rede.rede_restaurantes.repository.PedidoPromocaoRepository;
import com.rede.rede_restaurantes.repository.ProdutoUnidadeRepository;
import com.rede.rede_restaurantes.repository.PromocaoRepository;
import com.rede.rede_restaurantes.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class ItemPedidoService {

    private final ItemPedidoRepository itemPedidoRepository;
    private final PedidoRepository pedidoRepository;
    private final ProdutoUnidadeRepository produtoUnidadeRepository;
    private final EstoqueRepository estoqueRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoPromocaoRepository pedidoPromocaoRepository;
    private final PromocaoRepository promocaoRepository;

    public ItemPedidoService(
            ItemPedidoRepository itemPedidoRepository,
            PedidoRepository pedidoRepository,
            ProdutoUnidadeRepository produtoUnidadeRepository,
            EstoqueRepository estoqueRepository,
            UsuarioRepository usuarioRepository,
            PedidoPromocaoRepository pedidoPromocaoRepository,
            PromocaoRepository promocaoRepository) {

        this.itemPedidoRepository = itemPedidoRepository;
        this.pedidoRepository = pedidoRepository;
        this.produtoUnidadeRepository = produtoUnidadeRepository;
        this.estoqueRepository = estoqueRepository;
        this.usuarioRepository = usuarioRepository;
        this.pedidoPromocaoRepository = pedidoPromocaoRepository;
        this.promocaoRepository = promocaoRepository;
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

    @Transactional
    public ItemPedido salvar(ItemPedido itemPedido) {

        if (itemPedido.getPedidoId() == null) {
            throw new EntityNotFoundException("Pedido não encontrado");
        }

        Pedido pedido = verificarAcessoAoPedido(itemPedido.getPedidoId());

        validarPedidoEmAberto(pedido);

        if (itemPedido.getProdutoUnidadeId() == null) {
            throw new EntityNotFoundException(
                    "Produto da unidade não encontrado");
        }

        ProdutoUnidade produtoUnidade = produtoUnidadeRepository
                .findById(itemPedido.getProdutoUnidadeId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Produto da unidade não encontrado"));

        if (!produtoUnidade.getUnidadeId().equals(pedido.getUnidadeId())) {
            throw new IllegalArgumentException(
                    "O produto não pertence à unidade do pedido");
        }

        if (!Boolean.TRUE.equals(produtoUnidade.getDisponivel())) {
            throw new IllegalArgumentException(
                    "O produto não está disponível para venda");
        }

        if (itemPedido.getQuantidade() == null
                || itemPedido.getQuantidade() <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero");
        }

        Estoque estoque = estoqueRepository
                .findByProdutoUnidadeId(produtoUnidade.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Estoque do produto não encontrado"));

        if (estoque.getQuantidadeAtual() < itemPedido.getQuantidade()) {
            throw new IllegalArgumentException(
                    "Estoque insuficiente para o produto");
        }

        BigDecimal precoUnitario = produtoUnidade.getPreco();

        BigDecimal subtotal = precoUnitario.multiply(
                BigDecimal.valueOf(itemPedido.getQuantidade()));

        itemPedido.setPrecoUnitario(precoUnitario);
        itemPedido.setSubtotal(subtotal);

        ItemPedido itemSalvo = itemPedidoRepository.save(itemPedido);

        atualizarValoresPedido(pedido);

        return itemSalvo;
    }

    @Transactional
    public void excluir(Long id) {

        ItemPedido item = itemPedidoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Item do pedido não encontrado"));

        Pedido pedido = verificarAcessoAoPedido(item.getPedidoId());

        validarPedidoEmAberto(pedido);

        itemPedidoRepository.delete(item);

        atualizarValoresPedido(pedido);
    }

    private void validarPedidoEmAberto(Pedido pedido) {

        if (!"AGUARDANDO_PAGAMENTO".equals(pedido.getStatus())) {
            throw new IllegalArgumentException(
                    "Só é permitido alterar itens de pedidos aguardando pagamento");
        }
    }

    private void atualizarValoresPedido(Pedido pedido) {

        List<ItemPedido> itens =
                itemPedidoRepository.findByPedidoId(pedido.getId());

        BigDecimal subtotalGeral = itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal descontoTotal = BigDecimal.ZERO;

        List<PedidoPromocao> associacoes =
                pedidoPromocaoRepository.findByPedidoId(pedido.getId());

        for (PedidoPromocao associacao : associacoes) {

            Promocao promocao = promocaoRepository
                    .findById(associacao.getPromocaoId())
                    .orElseThrow(() ->
                            new EntityNotFoundException("Promoção não encontrada"));

            BigDecimal desconto;

            if ("PERCENTUAL".equals(promocao.getTipoDesconto())) {

                desconto = subtotalGeral
                        .multiply(promocao.getValorDesconto())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            } else if ("FIXO".equals(promocao.getTipoDesconto())) {

                desconto = promocao.getValorDesconto();

            } else {
                throw new IllegalArgumentException("Tipo de desconto inválido");
            }

            descontoTotal = descontoTotal.add(desconto);
        }

        if (descontoTotal.compareTo(subtotalGeral) > 0) {
            descontoTotal = subtotalGeral;
        }

        BigDecimal valorTotal = subtotalGeral.subtract(descontoTotal);

        pedido.setValorSubtotal(subtotalGeral);
        pedido.setValorDesconto(descontoTotal);
        pedido.setValorTotal(valorTotal);

        pedidoRepository.save(pedido);
    }
}
