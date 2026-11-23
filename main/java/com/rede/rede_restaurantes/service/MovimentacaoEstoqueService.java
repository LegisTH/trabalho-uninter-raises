package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.MovimentacaoEstoque;
import com.rede.rede_restaurantes.entity.Estoque;
import com.rede.rede_restaurantes.entity.ItemPedido;
import com.rede.rede_restaurantes.entity.Pedido;

import com.rede.rede_restaurantes.repository.MovimentacaoEstoqueRepository;
import com.rede.rede_restaurantes.repository.EstoqueRepository;
import com.rede.rede_restaurantes.repository.UsuarioRepository;
import com.rede.rede_restaurantes.repository.ItemPedidoRepository;
import com.rede.rede_restaurantes.repository.PedidoRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@PreAuthorize("hasRole('ADMIN')")
public class MovimentacaoEstoqueService {

    private final MovimentacaoEstoqueRepository movimentacaoEstoqueRepository;
    private final EstoqueRepository estoqueRepository;
    private final UsuarioRepository usuarioRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final PedidoRepository pedidoRepository;

    public MovimentacaoEstoqueService(
            MovimentacaoEstoqueRepository movimentacaoEstoqueRepository,
            EstoqueRepository estoqueRepository,
            UsuarioRepository usuarioRepository,
            ItemPedidoRepository itemPedidoRepository,
            PedidoRepository pedidoRepository) {

        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
        this.estoqueRepository = estoqueRepository;
        this.usuarioRepository = usuarioRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public List<MovimentacaoEstoque> listarTodos() {
        return movimentacaoEstoqueRepository.findAll();
    }

    public Optional<MovimentacaoEstoque> buscarPorId(Long id) {
        return movimentacaoEstoqueRepository.findById(id);
    }

    public List<MovimentacaoEstoque> listarPorEstoque(Long estoqueId) {
        return movimentacaoEstoqueRepository.findByEstoqueId(estoqueId);
    }

    public List<MovimentacaoEstoque> listarPorUsuario(Long usuarioId) {
        return movimentacaoEstoqueRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public MovimentacaoEstoque salvar(MovimentacaoEstoque movimentacao) {

        if (movimentacao.getEstoqueId() == null) {
            throw new EntityNotFoundException("Estoque não encontrado");
        }

        Estoque estoque = estoqueRepository.findById(movimentacao.getEstoqueId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Estoque não encontrado"));

        if (movimentacao.getUsuarioId() == null
                || !usuarioRepository.existsById(movimentacao.getUsuarioId())) {
            throw new EntityNotFoundException("Usuário não encontrado");
        }

        if (movimentacao.getQuantidade() == null
                || movimentacao.getQuantidade() <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero");
        }

        String tipo = movimentacao.getTipo();

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "Tipo de movimentação inválido");
        }

        if (tipo.equalsIgnoreCase("ENTRADA")) {

            estoque.setQuantidadeAtual(
                    Math.addExact(
                            estoque.getQuantidadeAtual(),
                            movimentacao.getQuantidade()));

        } else if (tipo.equalsIgnoreCase("SAIDA")) {

            if (estoque.getQuantidadeAtual() < movimentacao.getQuantidade()) {
                throw new IllegalArgumentException(
                        "Estoque insuficiente para realizar a saída");
            }

            estoque.setQuantidadeAtual(
                    estoque.getQuantidadeAtual() - movimentacao.getQuantidade());

        } else {
            throw new IllegalArgumentException(
                    "Tipo de movimentação deve ser ENTRADA ou SAIDA");
        }

        estoqueRepository.save(estoque);

        return movimentacaoEstoqueRepository.save(movimentacao);
    }

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void registrarSaidaPorPagamento(Long pedidoId, Long usuarioId) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Pedido não encontrado"));

        if (!pedido.getUsuarioId().equals(usuarioId)) {
            throw new IllegalArgumentException(
                    "O usuário não corresponde ao pedido");
        }

        if (!usuarioRepository.existsById(usuarioId)) {
            throw new EntityNotFoundException("Usuário não encontrado");
        }

        List<ItemPedido> itens = itemPedidoRepository.findByPedidoId(pedidoId);

        for (ItemPedido item : itens) {

            Estoque estoque = estoqueRepository
                    .findByProdutoUnidadeId(item.getProdutoUnidadeId())
                    .orElseThrow(() ->
                            new EntityNotFoundException(
                                    "Estoque do produto não encontrado"));

            if (estoque.getQuantidadeAtual() < item.getQuantidade()) {
                throw new IllegalArgumentException(
                        "Estoque insuficiente para o produto da unidade "
                                + item.getProdutoUnidadeId());
            }

            estoque.setQuantidadeAtual(
                    estoque.getQuantidadeAtual() - item.getQuantidade());

            estoqueRepository.save(estoque);

            MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();

            movimentacao.setEstoqueId(estoque.getId());
            movimentacao.setUsuarioId(usuarioId);
            movimentacao.setTipo("SAIDA");
            movimentacao.setQuantidade(item.getQuantidade());
            movimentacao.setMotivo(
                    "Saída referente ao pagamento aprovado do pedido " + pedidoId);

            movimentacaoEstoqueRepository.save(movimentacao);
        }
    }

    public void excluir(Long id) {
        movimentacaoEstoqueRepository.deleteById(id);
    }
}