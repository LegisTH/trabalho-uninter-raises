
package com.rede.rede_restaurantes.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.rede.rede_restaurantes.dto.RespostaGateway;
import com.rede.rede_restaurantes.dto.SolicitarPagamento;

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
import java.util.Map;
import java.util.Optional;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final GatewayPagamentoService gatewayPagamentoService;
    private final MovimentacaoEstoqueService movimentacaoEstoqueService;
    private final PedidoService pedidoService;
    private final ObjectMapper objectMapper;

    public PagamentoService(
            PagamentoRepository pagamentoRepository,
            PedidoRepository pedidoRepository,
            UsuarioRepository usuarioRepository,
            GatewayPagamentoService gatewayPagamentoService,
            MovimentacaoEstoqueService movimentacaoEstoqueService,
            PedidoService pedidoService,
            ObjectMapper objectMapper) {

        this.pagamentoRepository = pagamentoRepository;
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.gatewayPagamentoService = gatewayPagamentoService;
        this.movimentacaoEstoqueService = movimentacaoEstoqueService;
        this.pedidoService = pedidoService;
        this.objectMapper = objectMapper;
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

    private String converterParaJson(Object objeto) {

        try {
            return objectMapper.writeValueAsString(objeto);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Não foi possível gerar o JSON do pagamento", e);
        }
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
    @PreAuthorize("isAuthenticated()")
    public Pagamento solicitarPagamento(SolicitarPagamento dados) {

        Usuario usuario = usuarioAtual();

        Pedido pedido = pedidoRepository.findById(dados.getPedidoId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Pedido não encontrado"));

        if (!pedido.getUsuarioId().equals(usuario.getId())
                && !usuarioEhAdmin()) {

            throw new AccessDeniedException(
                    "Você não tem permissão para pagar este pedido");
        }

        Optional<Pagamento> pagamentoExistente =
                pagamentoRepository.findByChaveIdempotencia(
                        dados.getChaveIdempotencia());

        if (pagamentoExistente.isPresent()) {

            Pagamento existente = pagamentoExistente.get();

            if (!existente.getPedidoId().equals(pedido.getId())) {
                throw new IllegalArgumentException(
                        "Esta chave de idempotência já foi utilizada em outro pedido");
            }

            return existente;
        }

        if (!"AGUARDANDO_PAGAMENTO".equals(pedido.getStatus())) {
            throw new IllegalArgumentException(
                    "O pedido não está aguardando pagamento");
        }

        if (pedido.getValorTotal() == null
                || pedido.getValorTotal().signum() <= 0) {

            throw new IllegalArgumentException(
                    "O valor do pedido deve ser maior que zero");
        }

        boolean pagamentoExistenteParaPedido =
                pagamentoRepository.existsByPedidoIdAndStatusIn(
                        pedido.getId(),
                        List.of("PENDENTE", "APROVADO"));

        if (pagamentoExistenteParaPedido) {
            throw new IllegalArgumentException(
                    "Já existe um pagamento pendente ou aprovado para este pedido");
        }

        Map<String, Object> payloadEnviado = Map.of(
                "pedidoId", pedido.getId(),
                "metodoPagamento", dados.getMetodoPagamento(),
                "valor", pedido.getValorTotal(),
                "chaveIdempotencia", dados.getChaveIdempotencia()
        );

        RespostaGateway resposta =
                gatewayPagamentoService.processarPagamento(
                        dados.getMetodoPagamento(),
                        pedido.getValorTotal());

        Map<String, Object> payloadRetorno = Map.of(
                "aprovado", resposta.isAprovado(),
                "identificadorExterno", resposta.getIdentificadorExterno(),
                "status", resposta.getStatus(),
                "mensagem", resposta.getMensagem()
        );

        Pagamento pagamento = new Pagamento();

        pagamento.setPedidoId(pedido.getId());
        pagamento.setMetodoPagamento(dados.getMetodoPagamento());
        pagamento.setValor(pedido.getValorTotal());
        pagamento.setChaveIdempotencia(dados.getChaveIdempotencia());
        pagamento.setIdentificadorExterno(
                resposta.getIdentificadorExterno());
        pagamento.setStatus(resposta.getStatus());
        pagamento.setPayloadEnviado(converterParaJson(payloadEnviado));
        pagamento.setPayloadRetorno(converterParaJson(payloadRetorno));

        pagamento = pagamentoRepository.save(pagamento);

        if (resposta.isAprovado()) {

            movimentacaoEstoqueService.registrarSaidaPorPagamento(
                    pedido.getId(),
                    usuario.getId());

            pedidoService.confirmarPorPagamento(pedido.getId());
        }

        return pagamento;
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

        if ("APROVADO".equalsIgnoreCase(pagamento.getStatus())) {
            throw new IllegalArgumentException(
                    "Pagamentos aprovados devem ser processados pelo gateway");
        }

        boolean pagamentoExistente =
                pagamentoRepository.existsByPedidoIdAndStatusIn(
                        pagamento.getPedidoId(),
                        List.of("PENDENTE", "APROVADO"));

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
            throw new IllegalArgumentException(
                    "O status do pagamento é obrigatório");
        }

        if ("APROVADO".equalsIgnoreCase(dados.getStatus())
                && !"APROVADO".equals(pagamento.getStatus())) {

            throw new IllegalArgumentException(
                    "Pagamentos aprovados devem ser processados pelo gateway");
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
        pagamento.setIdentificadorExterno(
                dados.getIdentificadorExterno());
        pagamento.setPayloadEnviado(dados.getPayloadEnviado());
        pagamento.setPayloadRetorno(dados.getPayloadRetorno());

        return pagamentoRepository.save(pagamento);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void excluir(Long id) {

        Pagamento pagamento = pagamentoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Pagamento não encontrado"));

        if ("APROVADO".equals(pagamento.getStatus())) {
            throw new IllegalArgumentException(
                    "Não é permitido excluir um pagamento aprovado");
        }

        pagamentoRepository.delete(pagamento);
    }
}
