package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.Papel;
import com.rede.rede_restaurantes.repository.PapelRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PapelService {

    private final PapelRepository papelRepository;

    public PapelService(PapelRepository papelRepository) {
        this.papelRepository = papelRepository;
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

    public Papel salvar(Papel papel) {
        return papelRepository.save(papel);
    }

    public void excluir(Long id) {
        papelRepository.deleteById(id);
    }
}