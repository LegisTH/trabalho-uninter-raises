
package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.MovimentacaoEstoque;
import com.rede.rede_restaurantes.entity.Estoque;

import com.rede.rede_restaurantes.repository.MovimentacaoEstoqueRepository;
import com.rede.rede_restaurantes.repository.EstoqueRepository;
import com.rede.rede_restaurantes.repository.UsuarioRepository;

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

    public MovimentacaoEstoqueService(
            MovimentacaoEstoqueRepository movimentacaoEstoqueRepository,
            EstoqueRepository estoqueRepository,
            UsuarioRepository usuarioRepository) {

        this.movimentacaoEstoqueRepository = movimentacaoEstoqueRepository;
        this.estoqueRepository = estoqueRepository;
        this.usuarioRepository = usuarioRepository;
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

    public void excluir(Long id) {
        movimentacaoEstoqueRepository.deleteById(id);
    }
}
