
package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.Papel;
import com.rede.rede_restaurantes.repository.PapelRepository;
import com.rede.rede_restaurantes.repository.UsuarioPapelRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PapelService {

    private final PapelRepository papelRepository;
    private final UsuarioPapelRepository usuarioPapelRepository;

    public PapelService(
            PapelRepository papelRepository,
            UsuarioPapelRepository usuarioPapelRepository) {

        this.papelRepository = papelRepository;
        this.usuarioPapelRepository = usuarioPapelRepository;
    }

    public List<Papel> listarTodos() {
        return papelRepository.findAll();
    }

    public Optional<Papel> buscarPorId(Long id) {
        return papelRepository.findById(id);
    }

    public Optional<Papel> buscarPorNome(String nome) {
        return papelRepository.findByNome(nome);
    }

    @Transactional
    public Papel salvar(Papel papel) {

        Papel papelExistente = null;

        if (papel.getId() != null) {
            papelExistente = papelRepository.findById(papel.getId())
                    .orElseThrow(() ->
                            new EntityNotFoundException("Papel não encontrado"));
        }

        Optional<Papel> papelComNome =
                papelRepository.findByNome(papel.getNome());

        if (papelComNome.isPresent() &&
                (papelExistente == null ||
                !papelComNome.get().getId().equals(papelExistente.getId()))) {

            throw new IllegalArgumentException(
                    "Este nome de papel já está cadastrado");
        }

        return papelRepository.save(papel);
    }

    @Transactional
    public void excluir(Long id) {

        Papel papel = papelRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Papel não encontrado"));

        if (usuarioPapelRepository.existsByPapel_Id(id)) {
            throw new IllegalArgumentException(
                    "Não é possível excluir um papel associado a usuários");
        }

        papelRepository.delete(papel);
    }
}
