
package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.Auditoria;
import com.rede.rede_restaurantes.repository.AuditoriaRepository;
import com.rede.rede_restaurantes.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;

    public AuditoriaService(
            AuditoriaRepository auditoriaRepository,
            UsuarioRepository usuarioRepository) {

        this.auditoriaRepository = auditoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<Auditoria> listarTodas() {
        return auditoriaRepository.findAll();
    }

    public List<Auditoria> listarPorUsuario(Long usuarioId) {
        return auditoriaRepository.findByUsuarioId(usuarioId);
    }

    public List<Auditoria> listarPorEntidade(String entidadeAfetada) {
        return auditoriaRepository.findByEntidadeAfetada(entidadeAfetada);
    }

    public Auditoria salvar(Auditoria auditoria) {

        if (auditoria.getUsuarioId() != null
                && !usuarioRepository.existsById(auditoria.getUsuarioId())) {
            throw new EntityNotFoundException("Usuário não encontrado");
        }

        return auditoriaRepository.save(auditoria);
    }
}
