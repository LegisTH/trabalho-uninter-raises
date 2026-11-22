
package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.Estoque;
import com.rede.rede_restaurantes.repository.EstoqueRepository;
import com.rede.rede_restaurantes.repository.ProdutoUnidadeRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@PreAuthorize("hasRole('ADMIN')")
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;
    private final ProdutoUnidadeRepository produtoUnidadeRepository;

    public EstoqueService(
            EstoqueRepository estoqueRepository,
            ProdutoUnidadeRepository produtoUnidadeRepository) {

        this.estoqueRepository = estoqueRepository;
        this.produtoUnidadeRepository = produtoUnidadeRepository;
    }

    public List<Estoque> listarTodos() {
        return estoqueRepository.findAll();
    }

    public Optional<Estoque> buscarPorId(Long id) {
        return estoqueRepository.findById(id);
    }

    public Optional<Estoque> buscarPorProdutoUnidade(Long produtoUnidadeId) {
        return estoqueRepository.findByProdutoUnidadeId(produtoUnidadeId);
    }

    public Estoque salvar(Estoque estoque) {

        if (estoque.getProdutoUnidadeId() == null
                || !produtoUnidadeRepository.existsById(
                        estoque.getProdutoUnidadeId())) {
            throw new EntityNotFoundException(
                    "Produto da unidade não encontrado");
        }

        return estoqueRepository.save(estoque);
    }

    public void excluir(Long id) {
        estoqueRepository.deleteById(id);
    }
}
