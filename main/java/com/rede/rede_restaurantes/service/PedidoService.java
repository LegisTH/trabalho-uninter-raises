
package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.dto.CriarPedido;
import com.rede.rede_restaurantes.entity.Estoque;
import com.rede.rede_restaurantes.entity.ItemPedido;
import com.rede.rede_restaurantes.entity.Pedido;
import com.rede.rede_restaurantes.entity.ProdutoUnidade;
import com.rede.rede_restaurantes.entity.Usuario;
import com.rede.rede_restaurantes.repository.EstoqueRepository;
import com.rede.rede_restaurantes.repository.ItemPedidoRepository;
import com.rede.rede_restaurantes.repository.PedidoRepository;
import com.rede.rede_restaurantes.repository.ProdutoUnidadeRepository;
import com.rede.rede_restaurantes.repository.UnidadeRepository;
import com.rede.rede_restaurantes.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final ProdutoUnidadeRepository produtoUnidadeRepository;
    private final EstoqueRepository estoqueRepository;
    private final UnidadeRepository unidadeRepository;
    private final UsuarioRepository usuarioRepository;

    private static final Set<String> CANAIS_VALIDOS =
            Set.of("APP", "TOTEM", "BALCAO", "PICKUP", "WEB");

    public PedidoService(
            PedidoRepository pedidoRepository,
            ItemPedidoRepository itemPedidoRepository,
            ProdutoUnidadeRepository produtoUnidadeRepository,
            EstoqueRepository estoqueRepository,
            UnidadeRepository unidadeRepository,
            UsuarioRepository usuarioRepository) {

        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.produtoUnidadeRepository = produtoUnidadeRepository;
        this.estoqueRepository = estoqueRepository;
        this.unidadeRepository = unidadeRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Pedido salvar(CriarPedido dados) {

        Usuario usuario = usuarioAtual();

        if (dados.canalPedido() == null ||
                !CANAIS_VALIDOS.contains(dados.canalPedido().trim().toUpperCase())) {
            throw new IllegalArgumentException("Canal do pedido inválido");
        }

        if (!unidadeRepository.existsById(dados.unidadeId())) {
            throw new EntityNotFoundException("Unidade não encontrada");
        }

        Pedido pedido = new Pedido();
        pedido.setUsuarioId(usuario.getId());
        pedido.setUnidadeId(dados.unidadeId());
        pedido.setCanalPedido(dados.canalPedido().trim().toUpperCase());
        pedido.setObservacao(dados.observacao());
        pedido.setStatus("AGUARDANDO_PAGAMENTO");
        pedido.setValorSubtotal(BigDecimal.ZERO);
        pedido.setValorDesconto(BigDecimal.ZERO);
        pedido.setValorTotal(BigDecimal.ZERO);

        pedido = pedidoRepository.save(pedido);

        BigDecimal subtotalGeral = BigDecimal.ZERO;

        for (CriarPedido.Item itemDados : dados.itens()) {

            ProdutoUnidade produtoUnidade = produtoUnidadeRepository
                    .findById(itemDados.produtoUnidadeId())
                    .orElseThrow(() ->
                            new EntityNotFoundException("Produto da unidade não encontrado"));

            if (!produtoUnidade.getUnidadeId().equals(dados.unidadeId())) {
                throw new IllegalArgumentException(
                        "O produto não pertence à unidade informada");
            }

            if (!Boolean.TRUE.equals(produtoUnidade.getDisponivel())) {
                throw new IllegalArgumentException(
                        "O produto não está disponível para venda");
            }

            Estoque estoque = estoqueRepository
                    .findByProdutoUnidadeId(produtoUnidade.getId())
                    .orElseThrow(() ->
                            new EntityNotFoundException("Estoque do produto não encontrado"));

            if (estoque.getQuantidadeAtual() < itemDados.quantidade()) {
                throw new IllegalArgumentException(
                        "Estoque insuficiente para o produto da unidade: "
                                + produtoUnidade.getId());
            }

            BigDecimal precoUnitario = produtoUnidade.getPreco();

            BigDecimal subtotalItem = precoUnitario.multiply(
                    BigDecimal.valueOf(itemDados.quantidade()));

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedidoId(pedido.getId());
            itemPedido.setProdutoUnidadeId(produtoUnidade.getId());
            itemPedido.setQuantidade(itemDados.quantidade());
            itemPedido.setPrecoUnitario(precoUnitario);
            itemPedido.setSubtotal(subtotalItem);

            itemPedidoRepository.save(itemPedido);

            subtotalGeral = subtotalGeral.add(subtotalItem);
        }

        pedido.setValorSubtotal(subtotalGeral);
        pedido.setValorDesconto(BigDecimal.ZERO);
        pedido.setValorTotal(subtotalGeral);

        return pedidoRepository.save(pedido);
    }

    public List<Pedido> listarTodos() {

        if (usuarioEhAdmin()) {
            return pedidoRepository.findAll();
        }

        Usuario usuario = usuarioAtual();
        return pedidoRepository.findByUsuarioId(usuario.getId());
    }

    public Pedido buscarPorId(Long id) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Pedido não encontrado"));

        verificarAcesso(pedido);

        return pedido;
    }

    public List<Pedido> listarPorUsuario(Long usuarioId) {

        if (usuarioEhAdmin()) {
            return pedidoRepository.findByUsuarioId(usuarioId);
        }

        Usuario usuario = usuarioAtual();

        if (!usuario.getId().equals(usuarioId)) {
            throw new AccessDeniedException(
                    "Você não tem permissão para consultar pedidos de outro usuário");
        }

        return pedidoRepository.findByUsuarioId(usuarioId);
    }

    public List<Pedido> listarPorUnidade(Long unidadeId) {

        if (usuarioEhAdmin()) {
            return pedidoRepository.findByUnidadeId(unidadeId);
        }

        Usuario usuario = usuarioAtual();

        return pedidoRepository.findByUsuarioId(usuario.getId())
                .stream()
                .filter(pedido -> pedido.getUnidadeId().equals(unidadeId))
                .toList();
    }

    public List<Pedido> listarPorStatus(String status) {

        if (usuarioEhAdmin()) {
            return pedidoRepository.findByStatus(status);
        }

        Usuario usuario = usuarioAtual();

        return pedidoRepository.findByUsuarioId(usuario.getId())
                .stream()
                .filter(pedido -> pedido.getStatus().equalsIgnoreCase(status))
                .toList();
    }

    public List<Pedido> listarPorCanal(String canalPedido) {

        if (canalPedido == null || canalPedido.isBlank()) {
            return listarTodos();
        }

        String canal = canalPedido.trim().toUpperCase();

        if (!CANAIS_VALIDOS.contains(canal)) {
            throw new IllegalArgumentException("Canal do pedido inválido");
        }

        if (usuarioEhAdmin()) {
            return pedidoRepository.findByCanalPedido(canal);
        }

        Usuario usuario = usuarioAtual();

        return pedidoRepository.findByUsuarioId(usuario.getId())
                .stream()
                .filter(pedido -> pedido.getCanalPedido().equalsIgnoreCase(canal))
                .toList();
    }

    @Transactional
    public Pedido atualizar(Long id, Pedido dados) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Pedido não encontrado"));

        if (dados.getStatus() != null) {
            validarTransicaoStatus(pedido.getStatus(), dados.getStatus());
            pedido.setStatus(dados.getStatus());
        }

        if (dados.getObservacao() != null) {
            pedido.setObservacao(dados.getObservacao());
        }

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido confirmarPorPagamento(Long pedidoId) {

        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Pedido não encontrado"));

        if (!"AGUARDANDO_PAGAMENTO".equals(pedido.getStatus())) {
            throw new IllegalArgumentException(
                    "O pedido não está aguardando pagamento");
        }

        pedido.setStatus("CONFIRMADO");

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public void excluir(Long id) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Pedido não encontrado"));

        itemPedidoRepository.deleteAll(
                itemPedidoRepository.findByPedidoId(id));

        pedidoRepository.delete(pedido);
    }

    private void validarTransicaoStatus(String atual, String novo) {

        boolean permitida = switch (atual) {
            case "AGUARDANDO_PAGAMENTO" -> novo.equals("CANCELADO");
            case "PENDENTE" -> novo.equals("CONFIRMADO") || novo.equals("CANCELADO");
            case "CONFIRMADO" -> novo.equals("EM_PREPARACAO") || novo.equals("CANCELADO");
            case "EM_PREPARACAO" -> novo.equals("PRONTO") || novo.equals("CANCELADO");
            case "PRONTO" -> novo.equals("ENTREGUE") || novo.equals("CANCELADO");
            default -> false;
        };

        if (!permitida) {
            throw new IllegalArgumentException(
                    "Transição de status não permitida: "
                            + atual + " para " + novo);
        }
    }

    private void verificarAcesso(Pedido pedido) {

        if (usuarioEhAdmin()) {
            return;
        }

        Usuario usuario = usuarioAtual();

        if (!pedido.getUsuarioId().equals(usuario.getId())) {
            throw new AccessDeniedException(
                    "Você não tem permissão para acessar este pedido");
        }
    }

    private boolean usuarioEhAdmin() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return authentication != null &&
                authentication.getAuthorities().stream()
                        .anyMatch(authority ->
                                authority.getAuthority().equals("ROLE_ADMIN"));
    }

    private Usuario usuarioAtual() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Usuário não autenticado");
        }

        return usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new EntityNotFoundException("Usuário autenticado não encontrado"));
    }
}
