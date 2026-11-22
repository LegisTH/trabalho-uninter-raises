
package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.CarteiraPontuacao;
import com.rede.rede_restaurantes.repository.CarteiraPontuacaoRepository;
import com.rede.rede_restaurantes.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarteiraPontuacaoService {

    private final CarteiraPontuacaoRepository carteiraPontuacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public CarteiraPontuacaoService(
            CarteiraPontuacaoRepository carteiraPontuacaoRepository,
            UsuarioRepository usuarioRepository) {

        this.carteiraPontuacaoRepository = carteiraPontuacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<CarteiraPontuacao> listarTodos() {
        return carteiraPontuacaoRepository.findAll();
    }

    public Optional<CarteiraPontuacao> buscarPorId(Long id) {
        return carteiraPontuacaoRepository.findById(id);
    }

    public Optional<CarteiraPontuacao> buscarPorUsuario(Long usuarioId) {
        return carteiraPontuacaoRepository.findByUsuarioId(usuarioId);
    }

    public CarteiraPontuacao salvar(CarteiraPontuacao carteira) {

        if (carteira.getUsuarioId() == null
                || !usuarioRepository.existsById(carteira.getUsuarioId())) {
            throw new EntityNotFoundException("Usuário não encontrado");
        }

        return carteiraPontuacaoRepository.save(carteira);
    }

    public void excluir(Long id) {
        carteiraPontuacaoRepository.deleteById(id);
    }
}
