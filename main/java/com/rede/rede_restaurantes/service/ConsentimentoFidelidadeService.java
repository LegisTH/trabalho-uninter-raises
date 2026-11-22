
package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.ConsentimentoFidelidade;
import com.rede.rede_restaurantes.repository.ConsentimentoFidelidadeRepository;
import com.rede.rede_restaurantes.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

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

    public List<ConsentimentoFidelidade> listarTodos() {
        return consentimentoRepository.findAll();
    }

    public Optional<ConsentimentoFidelidade> buscarPorId(Long id) {
        return consentimentoRepository.findById(id);
    }

    public Optional<ConsentimentoFidelidade> buscarPorUsuario(Long usuarioId) {
        return consentimentoRepository.findByUsuarioId(usuarioId);
    }

    public ConsentimentoFidelidade salvar(ConsentimentoFidelidade consentimento) {

        if (consentimento.getUsuarioId() == null
                || !usuarioRepository.existsById(consentimento.getUsuarioId())) {
            throw new EntityNotFoundException("Usuário não encontrado");
        }

        return consentimentoRepository.save(consentimento);
    }

    public void excluir(Long id) {
        consentimentoRepository.deleteById(id);
    }
}
