package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.Promocao;
import com.rede.rede_restaurantes.repository.PromocaoRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PromocaoService {

    private final PromocaoRepository promocaoRepository;

    public PromocaoService(PromocaoRepository promocaoRepository) {
        this.promocaoRepository = promocaoRepository;
    }

    public List<Promocao> listarTodas() {
        return promocaoRepository.findAll();
    }

    public List<Promocao> listarAtivas() {
        return promocaoRepository.findByAtivoTrue();
    }

    public Optional<Promocao> buscarPorId(Long id) {
        return promocaoRepository.findById(id);
    }

    public List<Promocao> buscarPorNome(String nome) {
        return promocaoRepository.findByNomeContainingIgnoreCase(nome);
    }

    public Promocao salvar(Promocao promocao) {
        return promocaoRepository.save(promocao);
    }

    public void excluir(Long id) {
        promocaoRepository.deleteById(id);
    }
}