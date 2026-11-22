
package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.CarteiraPontuacao;
import com.rede.rede_restaurantes.entity.MovimentacaoPontuacao;
import com.rede.rede_restaurantes.repository.MovimentacaoPontuacaoRepository;
import com.rede.rede_restaurantes.repository.CarteiraPontuacaoRepository;
import com.rede.rede_restaurantes.repository.PedidoRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MovimentacaoPontuacaoService {

    private final MovimentacaoPontuacaoRepository movimentacaoPontuacaoRepository;
    private final CarteiraPontuacaoRepository carteiraPontuacaoRepository;
    private final PedidoRepository pedidoRepository;

    public MovimentacaoPontuacaoService(
            MovimentacaoPontuacaoRepository movimentacaoPontuacaoRepository,
            CarteiraPontuacaoRepository carteiraPontuacaoRepository,
            PedidoRepository pedidoRepository) {

        this.movimentacaoPontuacaoRepository = movimentacaoPontuacaoRepository;
        this.carteiraPontuacaoRepository = carteiraPontuacaoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public List<MovimentacaoPontuacao> listarTodos() {
        return movimentacaoPontuacaoRepository.findAll();
    }

    public Optional<MovimentacaoPontuacao> buscarPorId(Long id) {
        return movimentacaoPontuacaoRepository.findById(id);
    }

    public List<MovimentacaoPontuacao> listarPorCarteira(Long carteiraId) {
        return movimentacaoPontuacaoRepository.findByCarteiraId(carteiraId);
    }

    public List<MovimentacaoPontuacao> listarPorPedido(Long pedidoId) {
        return movimentacaoPontuacaoRepository.findByPedidoId(pedidoId);
    }

    @Transactional
    public MovimentacaoPontuacao salvar(MovimentacaoPontuacao movimentacao) {

        if (movimentacao.getCarteiraId() == null) {
            throw new IllegalArgumentException("A carteira é obrigatória");
        }

        CarteiraPontuacao carteira = carteiraPontuacaoRepository
                .findById(movimentacao.getCarteiraId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Carteira de pontuação não encontrada"));

        if (movimentacao.getPedidoId() != null
                && !pedidoRepository.existsById(movimentacao.getPedidoId())) {
            throw new EntityNotFoundException("Pedido não encontrado");
        }

        if (movimentacao.getQuantidade() == null || movimentacao.getQuantidade() <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade de pontos deve ser maior que zero");
        }

        if (movimentacao.getTipo() == null) {
            throw new IllegalArgumentException("O tipo de movimentação é obrigatório");
        }

        switch (movimentacao.getTipo().toUpperCase()) {

            case "CREDITO":
                carteira.setSaldo(Math.addExact(
                        carteira.getSaldo(), movimentacao.getQuantidade()));
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

        carteiraPontuacaoRepository.save(carteira);

        return movimentacaoPontuacaoRepository.save(movimentacao);
    }

    public void excluir(Long id) {
        movimentacaoPontuacaoRepository.deleteById(id);
    }
}
