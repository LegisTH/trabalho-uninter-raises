
package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.Auditoria;
import com.rede.rede_restaurantes.repository.AuditoriaRepository;
import com.rede.rede_restaurantes.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@PreAuthorize("hasRole('ADMIN')")
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

        if (!usuarioRepository.existsById(usuarioId)) {
            throw new EntityNotFoundException("Usuário não encontrado");
        }

        return auditoriaRepository.findByUsuarioId(usuarioId);
    }

    public List<Auditoria> listarPorEntidade(String entidadeAfetada) {
        return auditoriaRepository.findByEntidadeAfetada(entidadeAfetada);
    }

    @Transactional
    public Auditoria salvar(Auditoria auditoria) {

        if (auditoria.getUsuarioId() != null
                && !usuarioRepository.existsById(auditoria.getUsuarioId())) {
            throw new EntityNotFoundException("Usuário não encontrado");
        }

        // A data é definida pelo próprio sistema.
        auditoria.setFeitoEm(LocalDateTime.now());

        return auditoriaRepository.save(auditoria);
    }
}
