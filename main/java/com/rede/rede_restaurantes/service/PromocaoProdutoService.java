
package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.PromocaoProduto;
import com.rede.rede_restaurantes.repository.PromocaoProdutoRepository;
import com.rede.rede_restaurantes.repository.PromocaoRepository;
import com.rede.rede_restaurantes.repository.ProdutoRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PromocaoProdutoService {

    private final PromocaoProdutoRepository promocaoProdutoRepository;
    private final PromocaoRepository promocaoRepository;
    private final ProdutoRepository produtoRepository;

    public PromocaoProdutoService(
            PromocaoProdutoRepository promocaoProdutoRepository,
            PromocaoRepository promocaoRepository,
            ProdutoRepository produtoRepository) {

        this.promocaoProdutoRepository = promocaoProdutoRepository;
        this.promocaoRepository = promocaoRepository;
        this.produtoRepository = produtoRepository;
    }

    public List<PromocaoProduto> listarTodos() {
        return promocaoProdutoRepository.findAll();
    }

    public Optional<PromocaoProduto> buscarPorId(Long id) {
        return promocaoProdutoRepository.findById(id);
    }

    public List<PromocaoProduto> listarPorPromocao(Long promocaoId) {
        return promocaoProdutoRepository.findByPromocaoId(promocaoId);
    }

    public List<PromocaoProduto> listarPorProduto(Long produtoId) {
        return promocaoProdutoRepository.findByProdutoId(produtoId);
    }

    public PromocaoProduto salvar(PromocaoProduto promocaoProduto) {

        if (promocaoProduto.getPromocaoId() == null
                || !promocaoRepository.existsById(promocaoProduto.getPromocaoId())) {
            throw new EntityNotFoundException("Promoção não encontrada");
        }

        if (promocaoProduto.getProdutoId() == null
                || !produtoRepository.existsById(promocaoProduto.getProdutoId())) {
            throw new EntityNotFoundException("Produto não encontrado");
        }

        return promocaoProdutoRepository.save(promocaoProduto);
    }

    public void excluir(Long id) {
        promocaoProdutoRepository.deleteById(id);
    }
}
