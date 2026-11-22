
package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.ProdutoUnidade;
import com.rede.rede_restaurantes.repository.ProdutoUnidadeRepository;
import com.rede.rede_restaurantes.repository.ProdutoRepository;
import com.rede.rede_restaurantes.repository.UnidadeRepository;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoUnidadeService {

    private final ProdutoUnidadeRepository produtoUnidadeRepository;
    private final ProdutoRepository produtoRepository;
    private final UnidadeRepository unidadeRepository;

    public ProdutoUnidadeService(
            ProdutoUnidadeRepository produtoUnidadeRepository,
            ProdutoRepository produtoRepository,
            UnidadeRepository unidadeRepository) {
        this.produtoUnidadeRepository = produtoUnidadeRepository;
        this.produtoRepository = produtoRepository;
        this.unidadeRepository = unidadeRepository;
    }

    public List<ProdutoUnidade> listarTodas() {
        return produtoUnidadeRepository.findAll();
    }

    public Optional<ProdutoUnidade> buscarPorId(Long id) {
        return produtoUnidadeRepository.findById(id);
    }

    public Optional<ProdutoUnidade> buscarPorProdutoEUnidade(Long produtoId, Long unidadeId) {
        return produtoUnidadeRepository.findByProdutoIdAndUnidadeId(produtoId, unidadeId);
    }

    public List<ProdutoUnidade> listarPorUnidade(Long unidadeId) {
        return produtoUnidadeRepository.findByUnidadeId(unidadeId);
    }

    public ProdutoUnidade salvar(ProdutoUnidade produtoUnidade) {

        if (produtoUnidade.getProdutoId() == null ||
                !produtoRepository.existsById(produtoUnidade.getProdutoId())) {
            throw new EntityNotFoundException("Produto não encontrado");
        }

        if (produtoUnidade.getUnidadeId() == null ||
                !unidadeRepository.existsById(produtoUnidade.getUnidadeId())) {
            throw new EntityNotFoundException("Unidade não encontrada");
        }

        return produtoUnidadeRepository.save(produtoUnidade);
    }

    public void excluir(Long id) {
        produtoUnidadeRepository.deleteById(id);
    }
}
