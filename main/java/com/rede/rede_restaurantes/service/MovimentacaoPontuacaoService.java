
package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.CarteiraPontuacao;
import com.rede.rede_restaurantes.entity.MovimentacaoPontuacao;
import com.rede.rede_restaurantes.entity.Pedido;
import com.rede.rede_restaurantes.entity.Usuario;

import com.rede.rede_restaurantes.repository.MovimentacaoPontuacaoRepository;
import com.rede.rede_restaurantes.repository.CarteiraPontuacaoRepository;
import com.rede.rede_restaurantes.repository.PedidoRepository;
import com.rede.rede_restaurantes.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MovimentacaoPontuacaoService {

    private final MovimentacaoPontuacaoRepository movimentacaoPontuacaoRepository;
    private final CarteiraPontuacaoRepository carteiraPontuacaoRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;

    public MovimentacaoPontuacaoService(
            MovimentacaoPontuacaoRepository movimentacaoPontuacaoRepository,
            CarteiraPontuacaoRepository carteiraPontuacaoRepository,
            PedidoRepository pedidoRepository,
            UsuarioRepository usuarioRepository) {

        this.movimentacaoPontuacaoRepository = movimentacaoPontuacaoRepository;
        this.carteiraPontuacaoRepository = carteiraPontuacaoRepository;
        this.pedidoRepository = pedidoRepository;
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

    private void verificarAcessoCarteira(Long carteiraId) {
        if (usuarioEhAdmin()) {
            return;
        }

        CarteiraPontuacao carteira = carteiraPontuacaoRepository
                .findById(carteiraId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Carteira não encontrada"));

        if (!carteira.getUsuarioId().equals(usuarioAtual().getId())) {
            throw new AccessDeniedException(
                    "Você não tem permissão para acessar esta carteira");
        }
    }

    private void verificarAcessoPedido(Long pedidoId) {
        if (usuarioEhAdmin()) {
            return;
        }

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Pedido não encontrado"));

        if (!pedido.getUsuarioId().equals(usuarioAtual().getId())) {
            throw new AccessDeniedException(
                    "Você não tem permissão para acessar este pedido");
        }
    }

    public List<MovimentacaoPontuacao> listarTodos() {

        if (usuarioEhAdmin()) {
            return movimentacaoPontuacaoRepository.findAll();
        }

        Long usuarioId = usuarioAtual().getId();

        return movimentacaoPontuacaoRepository.findAll().stream()
                .filter(movimentacao -> {
                    CarteiraPontuacao carteira =
                            carteiraPontuacaoRepository
                                    .findById(movimentacao.getCarteiraId())
                                    .orElse(null);

                    return carteira != null
                            && carteira.getUsuarioId().equals(usuarioId);
                })
                .toList();
    }

    public Optional<MovimentacaoPontuacao> buscarPorId(Long id) {

        Optional<MovimentacaoPontuacao> movimentacao =
                movimentacaoPontuacaoRepository.findById(id);

        if (movimentacao.isEmpty() || usuarioEhAdmin()) {
            return movimentacao;
        }

        verificarAcessoCarteira(movimentacao.get().getCarteiraId());

        return movimentacao;
    }

    public List<MovimentacaoPontuacao> listarPorCarteira(Long carteiraId) {

        verificarAcessoCarteira(carteiraId);

        return movimentacaoPontuacaoRepository.findByCarteiraId(carteiraId);
    }

    public List<MovimentacaoPontuacao> listarPorPedido(Long pedidoId) {

        verificarAcessoPedido(pedidoId);

        return movimentacaoPontuacaoRepository.findByPedidoId(pedidoId);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public MovimentacaoPontuacao salvar(
            MovimentacaoPontuacao movimentacao) {

        if (movimentacao.getCarteiraId() == null) {
            throw new IllegalArgumentException(
                    "A carteira é obrigatória");
        }

        CarteiraPontuacao carteira = carteiraPontuacaoRepository
                .findById(movimentacao.getCarteiraId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Carteira de pontuação não encontrada"));

        if (movimentacao.getPedidoId() != null
                && !pedidoRepository.existsById(movimentacao.getPedidoId())) {

            throw new EntityNotFoundException("Pedido não encontrado");
        }

        if (movimentacao.getQuantidade() == null
                || movimentacao.getQuantidade() <= 0) {

            throw new IllegalArgumentException(
                    "A quantidade de pontos deve ser maior que zero");
        }

        if (movimentacao.getTipo() == null
                || movimentacao.getTipo().isBlank()) {

            throw new IllegalArgumentException(
                    "O tipo de movimentação é obrigatório");
        }

        String tipo = movimentacao.getTipo().trim().toUpperCase();

        switch (tipo) {

            case "CREDITO":
                carteira.setSaldo(Math.addExact(
                        carteira.getSaldo(),
                        movimentacao.getQuantidade()));
                break;

            case "DEBITO":

                if (carteira.getSaldo() < movimentacao.getQuantidade()) {
                    throw new IllegalArgumentException(
                            "Saldo de pontos insuficiente para realizar o débito");
                }

                carteira.setSaldo(
                        carteira.getSaldo() - movimentacao.getQuantidade());
                break;

            default:
                throw new IllegalArgumentException(
                        "Tipo de movimentação inválido. Use CREDITO ou DEBITO");
        }

        movimentacao.setTipo(tipo);

        carteiraPontuacaoRepository.save(carteira);

        return movimentacaoPontuacaoRepository.save(movimentacao);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void excluir(Long id) {

        MovimentacaoPontuacao movimentacao =
                movimentacaoPontuacaoRepository.findById(id)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Movimentação de pontuação não encontrada"));

        CarteiraPontuacao carteira = carteiraPontuacaoRepository
                .findById(movimentacao.getCarteiraId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Carteira de pontuação não encontrada"));

        if ("CREDITO".equalsIgnoreCase(movimentacao.getTipo())) {

            if (carteira.getSaldo() < movimentacao.getQuantidade()) {
                throw new IllegalArgumentException(
                        "Não é possível excluir esta movimentação: "
                        + "o saldo atual é insuficiente para estornar o crédito");
            }

            carteira.setSaldo(
                    carteira.getSaldo() - movimentacao.getQuantidade());

        } else if ("DEBITO".equalsIgnoreCase(movimentacao.getTipo())) {

            carteira.setSaldo(Math.addExact(
                    carteira.getSaldo(),
                    movimentacao.getQuantidade()));
        }

        carteiraPontuacaoRepository.save(carteira);
        movimentacaoPontuacaoRepository.delete(movimentacao);
    }
}
