
package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.Usuario;
import com.rede.rede_restaurantes.repository.UsuarioRepository;
import com.rede.rede_restaurantes.repository.UnidadeRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UnidadeRepository unidadeRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            UnidadeRepository unidadeRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.unidadeRepository = unidadeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Transactional
    public Usuario salvar(Usuario usuario) {

        if (usuario.getUnidadeId() != null &&
                !unidadeRepository.existsById(usuario.getUnidadeId())) {
            throw new EntityNotFoundException("Unidade não encontrada");
        }

        Usuario usuarioExistente = null;

        if (usuario.getId() != null) {
            usuarioExistente = usuarioRepository.findById(usuario.getId())
                    .orElseThrow(() ->
                            new EntityNotFoundException("Usuário não encontrado"));
        }

        Optional<Usuario> usuarioComEmail =
                usuarioRepository.findByEmail(usuario.getEmail());

        if (usuarioComEmail.isPresent() &&
                (usuarioExistente == null ||
                !usuarioComEmail.get().getId().equals(usuarioExistente.getId()))) {
            throw new IllegalArgumentException(
                    "Este e-mail já está cadastrado");
        }

        if (usuarioExistente != null) {

            if (usuario.getSenhaHash() == null ||
                    usuario.getSenhaHash().isBlank()) {
                usuario.setSenhaHash(usuarioExistente.getSenhaHash());
            } else {
                usuario.setSenhaHash(
                        passwordEncoder.encode(usuario.getSenhaHash()));
            }

            usuario.setDataCriacao(usuarioExistente.getDataCriacao());

        } else {

            if (usuario.getSenhaHash() == null ||
                    usuario.getSenhaHash().isBlank()) {
                throw new IllegalArgumentException(
                        "A senha do usuário é obrigatória");
            }

            usuario.setSenhaHash(
                    passwordEncoder.encode(usuario.getSenhaHash()));
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void excluir(Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Usuário não encontrado"));

        usuarioRepository.delete(usuario);
    }
}
