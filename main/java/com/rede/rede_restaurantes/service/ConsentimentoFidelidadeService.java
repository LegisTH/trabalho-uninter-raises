
package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.ConsentimentoFidelidade;
import com.rede.rede_restaurantes.entity.Usuario;

import com.rede.rede_restaurantes.repository.ConsentimentoFidelidadeRepository;
import com.rede.rede_restaurantes.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ConsentimentoFidelidadeService {

    private final ConsentimentoFidelidadeRepository consentimentoRepository;
    private final UsuarioRepository usuarioRepository;

    public ConsentimentoFidelidadeService(
            ConsentimentoFidelidadeRepository consentimentoRepository,
            UsuarioRepository usuarioRepository) {

        this.consentimentoRepository = consentimentoRepository;
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

    @PreAuthorize("hasRole('ADMIN')")
    public List<ConsentimentoFidelidade> listarTodos() {
        return consentimentoRepository.findAll();
    }

    public Optional<ConsentimentoFidelidade> buscarPorId(Long id) {

        Optional<ConsentimentoFidelidade> consentimento =
                consentimentoRepository.findById(id);

        if (consentimento.isEmpty() || usuarioEhAdmin()) {
            return consentimento;
        }

        if (!consentimento.get().getUsuarioId()
                .equals(usuarioAtual().getId())) {

            throw new AccessDeniedException(
                    "Você não tem permissão para acessar este consentimento");
        }

        return consentimento;
    }

    public Optional<ConsentimentoFidelidade> buscarPorUsuario(Long usuarioId) {

        if (!usuarioEhAdmin()
                && !usuarioAtual().getId().equals(usuarioId)) {

            throw new AccessDeniedException(
                    "Você não tem permissão para acessar este consentimento");
        }

        return consentimentoRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public ConsentimentoFidelidade salvar(
            ConsentimentoFidelidade consentimento) {

        if (consentimento.getAceito() == null) {
            throw new IllegalArgumentException(
                    "É necessário informar se o consentimento foi aceito");
        }

        if (usuarioEhAdmin()) {

            if (consentimento.getUsuarioId() == null
                    || !usuarioRepository.existsById(
                            consentimento.getUsuarioId())) {

                throw new EntityNotFoundException("Usuário não encontrado");
            }

        } else {
            consentimento.setUsuarioId(usuarioAtual().getId());
        }

        if (consentimentoRepository
                .findByUsuarioId(consentimento.getUsuarioId()).isPresent()) {

            throw new IllegalArgumentException(
                    "Este usuário já possui um registro de consentimento. "
                    + "Utilize a atualização para alterar sua decisão");
        }

        return consentimentoRepository.save(consentimento);
    }

    @Transactional
    public ConsentimentoFidelidade atualizar(
            Long id, ConsentimentoFidelidade dados) {

        ConsentimentoFidelidade existente =
                consentimentoRepository.findById(id)
                        .orElseThrow(() ->
                                new EntityNotFoundException(
                                        "Consentimento não encontrado"));

        if (!usuarioEhAdmin()
                && !existente.getUsuarioId()
                        .equals(usuarioAtual().getId())) {

            throw new AccessDeniedException(
                    "Você não tem permissão para alterar este consentimento");
        }

        if (dados.getAceito() == null) {
            throw new IllegalArgumentException(
                    "É necessário informar se o consentimento foi aceito");
        }

        existente.setAceito(dados.getAceito());

        return consentimentoRepository.save(existente);
    }
}
