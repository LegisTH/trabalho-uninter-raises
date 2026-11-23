
package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.Pedido;
import com.rede.rede_restaurantes.entity.PedidoPromocao;
import com.rede.rede_restaurantes.entity.Promocao;
import com.rede.rede_restaurantes.repository.PedidoPromocaoRepository;
import com.rede.rede_restaurantes.repository.PedidoRepository;
import com.rede.rede_restaurantes.repository.PromocaoRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoPromocaoService {

    private final PedidoPromocaoRepository pedidoPromocaoRepository;
    private final PedidoRepository pedidoRepository;
    private final PromocaoRepository promocaoRepository;

    public PedidoPromocaoService(
            PedidoPromocaoRepository pedidoPromocaoRepository,
            PedidoRepository pedidoRepository,
            PromocaoRepository promocaoRepository) {

        this.pedidoPromocaoRepository = pedidoPromocaoRepository;
        this.pedidoRepository = pedidoRepository;
        this.promocaoRepository = promocaoRepository;
    }

    public List<PedidoPromocao> listarTodos() {
        return pedidoPromocaoRepository.findAll();
    }

    public Optional<PedidoPromocao> buscarPorId(Long id) {
        return pedidoPromocaoRepository.findById(id);
    }

    public List<PedidoPromocao> listarPorPedido(Long pedidoId) {
        return pedidoPromocaoRepository.findByPedidoId(pedidoId);
    }

    public List<PedidoPromocao> listarPorPromocao(Long promocaoId) {
        return pedidoPromocaoRepository.findByPromocaoId(promocaoId);
    }

    @Transactional
    public PedidoPromocao salvar(PedidoPromocao pedidoPromocao) {

        if (pedidoPromocao.getPedidoId() == null) {
            throw new IllegalArgumentException("O pedido é obrigatório");
        }

        Pedido pedido = pedidoRepository.findById(pedidoPromocao.getPedidoId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Pedido não encontrado"));

        if (!"AGUARDANDO_PAGAMENTO".equals(pedido.getStatus())) {
            throw new IllegalArgumentException(
                    "Só é possível associar promoções a pedidos aguardando pagamento");
        }

        if (pedidoPromocao.getPromocaoId() == null) {
            throw new IllegalArgumentException("A promoção é obrigatória");
        }

        Promocao promocao = promocaoRepository.findById(pedidoPromocao.getPromocaoId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Promoção não encontrada"));

        if (!Boolean.TRUE.equals(promocao.getAtivo())) {
            throw new IllegalArgumentException("Esta promoção está inativa");
        }

        LocalDateTime agora = LocalDateTime.now();

        if (agora.isBefore(promocao.getDataInicio())
                || agora.isAfter(promocao.getDataFim())) {
            throw new IllegalArgumentException(
                    "Esta promoção está fora do período de validade");
        }

        boolean associacaoDuplicada =
                pedidoPromocaoRepository.existsByPedidoIdAndPromocaoId(
                        pedidoPromocao.getPedidoId(),
                        pedidoPromocao.getPromocaoId());

        if (associacaoDuplicada) {
            throw new IllegalArgumentException(
                    "Esta promoção já está associada a este pedido");
        }

        PedidoPromocao associacaoSalva =
                pedidoPromocaoRepository.save(pedidoPromocao);

        atualizarDescontoPedido(pedido);

        return associacaoSalva;
    }

    @Transactional
    public void excluir(Long id) {

        PedidoPromocao pedidoPromocao = pedidoPromocaoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Associação entre pedido e promoção não encontrada"));

        Pedido pedido = pedidoRepository.findById(pedidoPromocao.getPedidoId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Pedido não encontrado"));

        if (!"AGUARDANDO_PAGAMENTO".equals(pedido.getStatus())) {
            throw new IllegalArgumentException(
                    "Só é possível remover promoções de pedidos aguardando pagamento");
        }

        pedidoPromocaoRepository.delete(pedidoPromocao);

        atualizarDescontoPedido(pedido);
    }

    private void atualizarDescontoPedido(Pedido pedido) {

        List<PedidoPromocao> associacoes =
                pedidoPromocaoRepository.findByPedidoId(pedido.getId());

        BigDecimal descontoTotal = BigDecimal.ZERO;

        for (PedidoPromocao associacao : associacoes) {

            Promocao promocao = promocaoRepository.findById(associacao.getPromocaoId())
                    .orElseThrow(() ->
                            new EntityNotFoundException("Promoção não encontrada"));

            BigDecimal desconto;

            if ("PERCENTUAL".equals(promocao.getTipoDesconto())) {

                desconto = pedido.getValorSubtotal()
                        .multiply(promocao.getValorDesconto())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            } else if ("FIXO".equals(promocao.getTipoDesconto())) {

                desconto = promocao.getValorDesconto();

            } else {
                throw new IllegalArgumentException("Tipo de desconto inválido");
            }

            descontoTotal = descontoTotal.add(desconto);
        }

        if (descontoTotal.compareTo(pedido.getValorSubtotal()) > 0) {
            descontoTotal = pedido.getValorSubtotal();
        }

        BigDecimal valorTotal = pedido.getValorSubtotal().subtract(descontoTotal);

        pedido.setValorDesconto(descontoTotal);
        pedido.setValorTotal(valorTotal);

        pedidoRepository.save(pedido);
    }
}
