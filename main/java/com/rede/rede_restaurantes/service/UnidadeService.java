
package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.Unidade;
import com.rede.rede_restaurantes.repository.UnidadeRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UnidadeService {

    private final UnidadeRepository unidadeRepository;

    public UnidadeService(UnidadeRepository unidadeRepository) {
        this.unidadeRepository = unidadeRepository;
    }

    public List<Unidade> listarTodas() {
        return unidadeRepository.findAll();
    }

    public Unidade buscarPorId(Long id) {
        return unidadeRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Unidade não encontrada"));
    }

    public Unidade salvar(Unidade unidade) {
        return unidadeRepository.save(unidade);
    }

    @Transactional
    public void excluir(Long id) {

        Unidade unidade = buscarPorId(id);

        unidadeRepository.delete(unidade);
    }
}
