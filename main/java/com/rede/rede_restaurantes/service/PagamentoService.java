
package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.Pagamento;
import com.rede.rede_restaurantes.entity.Pedido;
import com.rede.rede_restaurantes.entity.Usuario;
import com.rede.rede_restaurantes.repository.PagamentoRepository;
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
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;

    public PagamentoService(
            PagamentoRepository pagamentoRepository,
            PedidoRepository pedidoRepository,
            UsuarioRepository usuarioRepository) {

        this.pagamentoRepository = pagamentoRepository;
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

    private Pedido verificarAcessoAoPedido(Long pedidoId) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Pedido não encontrado"));

        if (!usuarioEhAdmin()
                && !pedido.getUsuarioId().equals(usuarioAtual().getId())) {

            throw new AccessDeniedException(
                    "Você não tem permissão para acessar este pagamento");
        }

        return pedido;
    }

    public List<Pagamento> listarTodos() {

        if (usuarioEhAdmin()) {
            return pagamentoRepository.findAll();
        }

        Usuario usuario = usuarioAtual();

        return pagamentoRepository.findAll().stream()
                .filter(pagamento -> {
                    Optional<Pedido> pedido =
                            pedidoRepository.findById(pagamento.getPedidoId());

                    return pedido.isPresent()
                            && pedido.get().getUsuarioId().equals(usuario.getId());
                })
                .toList();
    }

    public Optional<Pagamento> buscarPorId(Long id) {

        Optional<Pagamento> pagamento = pagamentoRepository.findById(id);

        if (pagamento.isEmpty() || usuarioEhAdmin()) {
            return pagamento;
        }

        verificarAcessoAoPedido(pagamento.get().getPedidoId());

        return pagamento;
    }

    public List<Pagamento> listarPorPedido(Long pedidoId) {

        verificarAcessoAoPedido(pedidoId);

        return pagamentoRepository.findByPedidoId(pedidoId);
    }

    public Optional<Pagamento> buscarPorIdentificadorExterno(
            String identificador) {

        Optional<Pagamento> pagamento =
                pagamentoRepository.findByIdentificadorExterno(identificador);

        if (pagamento.isEmpty() || usuarioEhAdmin()) {
            return pagamento;
        }

        verificarAcessoAoPedido(pagamento.get().getPedidoId());

        return pagamento;
    }

    public List<Pagamento> listarPorStatus(String status) {

        if (!usuarioEhAdmin()) {
            throw new AccessDeniedException(
                    "Somente administradores podem consultar pagamentos por status");
        }

        return pagamentoRepository.findByStatus(status);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Pagamento salvar(Pagamento pagamento) {

        if (pagamento.getPedidoId() == null) {
            throw new IllegalArgumentException("O pedido é obrigatório");
        }

        Pedido pedido = pedidoRepository.findById(pagamento.getPedidoId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Pedido não encontrado"));

        if (pagamento.getValor() == null
                || pedido.getValorTotal() == null
                || pagamento.getValor().compareTo(pedido.getValorTotal()) != 0) {

            throw new IllegalArgumentException(
                    "O valor do pagamento deve ser igual ao valor total do pedido");
        }

        boolean pagamentoExistente =
                pagamentoRepository.existsByPedidoIdAndStatusIn(
                        pagamento.getPedidoId(),
                        List.of("PENDENTE", "APROVADO")
                );

        if (pagamentoExistente) {
            throw new IllegalArgumentException(
                    "Já existe um pagamento pendente ou aprovado para este pedido");
        }

        return pagamentoRepository.save(pagamento);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Pagamento atualizar(Long id, Pagamento dados) {

        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Pagamento não encontrado"));

        if (dados.getStatus() == null || dados.getStatus().isBlank()) {
            throw new IllegalArgumentException("O status do pagamento é obrigatório");
        }

        if (dados.getStatus().equals("PENDENTE")
                || dados.getStatus().equals("APROVADO")) {

            boolean outroPagamento =
                    pagamentoRepository.existsByPedidoIdAndStatusInAndIdNot(
                            pagamento.getPedidoId(),
                            List.of("PENDENTE", "APROVADO"),
                            id
                    );

            if (outroPagamento) {
                throw new IllegalArgumentException(
                        "Já existe outro pagamento pendente ou aprovado para este pedido");
            }
        }

        pagamento.setStatus(dados.getStatus());
        pagamento.setIdentificadorExterno(dados.getIdentificadorExterno());
        pagamento.setPayloadEnviado(dados.getPayloadEnviado());
        pagamento.setPayloadRetorno(dados.getPayloadRetorno());

        return pagamentoRepository.save(pagamento);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void excluir(Long id) {

        if (!pagamentoRepository.existsById(id)) {
            throw new EntityNotFoundException("Pagamento não encontrado");
        }

        pagamentoRepository.deleteById(id);
    }
}
