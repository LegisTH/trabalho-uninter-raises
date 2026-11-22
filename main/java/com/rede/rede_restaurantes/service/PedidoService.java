package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.Pedido;
import com.rede.rede_restaurantes.entity.Usuario;
import com.rede.rede_restaurantes.repository.PedidoRepository;
import com.rede.rede_restaurantes.repository.UsuarioRepository;
import com.rede.rede_restaurantes.repository.UnidadeRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UnidadeRepository unidadeRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            UsuarioRepository usuarioRepository,
            UnidadeRepository unidadeRepository) {

        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.unidadeRepository = unidadeRepository;
    }

    private Authentication autenticacaoAtual() {
        Authentication autenticacao =
                SecurityContextHolder.getContext().getAuthentication();

        if (autenticacao == null || !autenticacao.isAuthenticated()) {
            throw new AccessDeniedException("Usuário não autenticado");
        }

        return autenticacao;
    }

    private boolean usuarioEhAdmin() {
        return autenticacaoAtual().getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));
    }

    private Usuario usuarioAtual() {
        String email = autenticacaoAtual().getName();

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new EntityNotFoundException("Usuário não encontrado"));
    }

    private void exigirAdmin() {
        if (!usuarioEhAdmin()) {
            throw new AccessDeniedException(
                    "Você não tem permissão para realizar esta operação");
        }
    }

    public List<Pedido> listarTodos() {

        if (usuarioEhAdmin()) {
            return pedidoRepository.findAll();
        }

        return pedidoRepository.findByUsuarioId(
                usuarioAtual().getId());
    }

    public Optional<Pedido> buscarPorId(Long id) {

        Optional<Pedido> pedido = pedidoRepository.findById(id);

        if (pedido.isEmpty() || usuarioEhAdmin()) {
            return pedido;
        }

        Long usuarioId = usuarioAtual().getId();

        if (!pedido.get().getUsuarioId().equals(usuarioId)) {
            return Optional.empty();
        }

        return pedido;
    }

    public List<Pedido> listarPorUsuario(Long usuarioId) {

        if (usuarioEhAdmin()) {
            return pedidoRepository.findByUsuarioId(usuarioId);
        }

        Long usuarioAtualId = usuarioAtual().getId();

        if (!usuarioAtualId.equals(usuarioId)) {
            throw new AccessDeniedException(
                    "Você só pode consultar seus próprios pedidos");
        }

        return pedidoRepository.findByUsuarioId(usuarioAtualId);
    }

    public List<Pedido> listarPorUnidade(Long unidadeId) {

        exigirAdmin();

        return pedidoRepository.findByUnidadeId(unidadeId);
    }

    public List<Pedido> listarPorStatus(String status) {

        exigirAdmin();

        return pedidoRepository.findByStatus(status);
    }

    public Pedido salvar(Pedido pedido) {

        if (!usuarioEhAdmin()) {
            pedido.setUsuarioId(usuarioAtual().getId());
        }

        if (pedido.getUsuarioId() == null
                || !usuarioRepository.existsById(pedido.getUsuarioId())) {
            throw new EntityNotFoundException("Usuário não encontrado");
        }

        if (pedido.getUnidadeId() == null
                || !unidadeRepository.existsById(pedido.getUnidadeId())) {
            throw new EntityNotFoundException("Unidade não encontrada");
        }

        return pedidoRepository.save(pedido);
    }

    public Pedido atualizar(Long id, Pedido dadosAtualizados) {

        exigirAdmin();

        Pedido pedidoExistente = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Pedido não encontrado"));

        String statusAtual = pedidoExistente.getStatus();
        String novoStatus = dadosAtualizados.getStatus();

        if (statusAtual.equalsIgnoreCase("CANCELADO")
                || statusAtual.equalsIgnoreCase("ENTREGUE")) {
            throw new IllegalArgumentException(
                    "Não é permitido alterar um pedido cancelado ou entregue");
        }

        if (novoStatus == null || novoStatus.isBlank()) {
            throw new IllegalArgumentException(
                    "Novo status não informado");
        }

        boolean transicaoValida =
                (statusAtual.equalsIgnoreCase("PENDENTE")
                        && (novoStatus.equalsIgnoreCase("CONFIRMADO")
                        || novoStatus.equalsIgnoreCase("CANCELADO")))
                || (statusAtual.equalsIgnoreCase("CONFIRMADO")
                        && (novoStatus.equalsIgnoreCase("EM_PREPARACAO")
                        || novoStatus.equalsIgnoreCase("CANCELADO")))
                || (statusAtual.equalsIgnoreCase("EM_PREPARACAO")
                        && (novoStatus.equalsIgnoreCase("PRONTO")
                        || novoStatus.equalsIgnoreCase("CANCELADO")))
                || (statusAtual.equalsIgnoreCase("PRONTO")
                        && (novoStatus.equalsIgnoreCase("ENTREGUE")
                        || novoStatus.equalsIgnoreCase("CANCELADO")));

        if (!transicaoValida) {
            throw new IllegalArgumentException(
                    "Transição de status inválida: "
                            + statusAtual + " para " + novoStatus);
        }

        pedidoExistente.setStatus(novoStatus);
        pedidoExistente.setObservacao(
                dadosAtualizados.getObservacao());

        return pedidoRepository.save(pedidoExistente);
    }

    public void excluir(Long id) {

        exigirAdmin();

        pedidoRepository.deleteById(id);
    }
}