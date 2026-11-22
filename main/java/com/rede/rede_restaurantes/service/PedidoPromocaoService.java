
package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.PedidoPromocao;
import com.rede.rede_restaurantes.repository.PedidoPromocaoRepository;
import com.rede.rede_restaurantes.repository.PedidoRepository;
import com.rede.rede_restaurantes.repository.PromocaoRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

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

    public PedidoPromocao salvar(PedidoPromocao pedidoPromocao) {

        if (pedidoPromocao.getPedidoId() == null
                || !pedidoRepository.existsById(pedidoPromocao.getPedidoId())) {
            throw new EntityNotFoundException("Pedido não encontrado");
        }

        if (pedidoPromocao.getPromocaoId() == null
                || !promocaoRepository.existsById(pedidoPromocao.getPromocaoId())) {
            throw new EntityNotFoundException("Promoção não encontrada");
        }

        return pedidoPromocaoRepository.save(pedidoPromocao);
    }

    public void excluir(Long id) {
        pedidoPromocaoRepository.deleteById(id);
    }
}
