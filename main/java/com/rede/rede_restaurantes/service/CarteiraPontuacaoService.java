
package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.CarteiraPontuacao;
import com.rede.rede_restaurantes.entity.Usuario;
import com.rede.rede_restaurantes.repository.CarteiraPontuacaoRepository;
import com.rede.rede_restaurantes.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private boolean usuarioEhAdmin() {
        Authentication autenticacao =
                SecurityContextHolder.getContext().getAuthentication();

        return autenticacao != null
                && autenticacao.getAuthorities().stream()
                .anyMatch(autoridade ->
                        autoridade.getAuthority().equals("ROLE_ADMIN"));
    }

    private Usuario usuarioAtual() {
        Authentication autenticacao =
                SecurityContextHolder.getContext().getAuthentication();

        if (autenticacao == null || !autenticacao.isAuthenticated()) {
            throw new AccessDeniedException("Usuário não autenticado");
        }

        return usuarioRepository.findByEmail(autenticacao.getName())
                .orElseThrow(() ->
                        new EntityNotFoundException("Usuário não encontrado"));
    }

    public List<CarteiraPontuacao> listarTodos() {

        if (usuarioEhAdmin()) {
            return carteiraPontuacaoRepository.findAll();
        }

        Long usuarioId = usuarioAtual().getId();

        return carteiraPontuacaoRepository.findAll().stream()
                .filter(carteira ->
                        carteira.getUsuarioId().equals(usuarioId))
                .toList();
    }

    public Optional<CarteiraPontuacao> buscarPorId(Long id) {

        Optional<CarteiraPontuacao> carteira =
                carteiraPontuacaoRepository.findById(id);

        if (carteira.isEmpty() || usuarioEhAdmin()) {
            return carteira;
        }

        if (!carteira.get().getUsuarioId().equals(usuarioAtual().getId())) {
            throw new AccessDeniedException(
                    "Você não tem permissão para acessar esta carteira");
        }

        return carteira;
    }

    public Optional<CarteiraPontuacao> buscarPorUsuario(Long usuarioId) {

        if (!usuarioEhAdmin()
                && !usuarioAtual().getId().equals(usuarioId)) {

            throw new AccessDeniedException(
                    "Você não tem permissão para acessar esta carteira");
        }

        return carteiraPontuacaoRepository.findByUsuarioId(usuarioId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public CarteiraPontuacao salvar(CarteiraPontuacao carteira) {

        if (carteira.getUsuarioId() == null
                || !usuarioRepository.existsById(carteira.getUsuarioId())) {

            throw new EntityNotFoundException("Usuário não encontrado");
        }

        carteira.setSaldo(0);

        return carteiraPontuacaoRepository.save(carteira);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void excluir(Long id) {

        if (!carteiraPontuacaoRepository.existsById(id)) {
            throw new EntityNotFoundException("Carteira não encontrada");
        }

        carteiraPontuacaoRepository.deleteById(id);
    }
}
