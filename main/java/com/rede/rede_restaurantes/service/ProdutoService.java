
package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.Produto;
import com.rede.rede_restaurantes.repository.ProdutoRepository;
import com.rede.rede_restaurantes.repository.CategoriaRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProdutoService(
            ProdutoRepository produtoRepository,
            CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }

    public Produto salvar(Produto produto) {

        if (produto.getCategoriaId() == null ||
                !categoriaRepository.existsById(produto.getCategoriaId())) {
            throw new jakarta.persistence.EntityNotFoundException(
                    "Categoria não encontrada");
        }

        return produtoRepository.save(produto);
    }

    public void excluir(Long id) {
        produtoRepository.deleteById(id);
    }
}
