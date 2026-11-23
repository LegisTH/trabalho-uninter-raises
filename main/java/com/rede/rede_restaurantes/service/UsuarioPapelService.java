
package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.Papel;
import com.rede.rede_restaurantes.entity.Usuario;
import com.rede.rede_restaurantes.entity.UsuarioPapel;
import com.rede.rede_restaurantes.entity.UsuarioPapelId;

import com.rede.rede_restaurantes.repository.PapelRepository;
import com.rede.rede_restaurantes.repository.UsuarioRepository;
import com.rede.rede_restaurantes.repository.UsuarioPapelRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioPapelService {

    private final UsuarioPapelRepository usuarioPapelRepository;
    private final UsuarioRepository usuarioRepository;
    private final PapelRepository papelRepository;

    public UsuarioPapelService(
            UsuarioPapelRepository usuarioPapelRepository,
            UsuarioRepository usuarioRepository,
            PapelRepository papelRepository) {

        this.usuarioPapelRepository = usuarioPapelRepository;
        this.usuarioRepository = usuarioRepository;
        this.papelRepository = papelRepository;
    }

    public List<UsuarioPapel> listarTodos() {
        return usuarioPapelRepository.findAll();
    }

    public List<UsuarioPapel> buscarPorUsuario(Long usuarioId) {

        if (!usuarioRepository.existsById(usuarioId)) {
            throw new EntityNotFoundException("Usuário não encontrado");
        }

        return usuarioPapelRepository.findByUsuario_Id(usuarioId);
    }

    @Transactional
    public UsuarioPapel associar(Long usuarioId, Long papelId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Usuário não encontrado"));

        Papel papel = papelRepository.findById(papelId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Papel não encontrado"));

        if (usuarioPapelRepository
                .existsByUsuario_IdAndPapel_Id(usuarioId, papelId)) {

            throw new IllegalArgumentException(
                    "Este usuário já possui esse papel");
        }

        UsuarioPapel usuarioPapel = new UsuarioPapel(usuario, papel);

        return usuarioPapelRepository.save(usuarioPapel);
    }

    @Transactional
    public void remover(Long usuarioId, Long papelId) {

        UsuarioPapelId id = new UsuarioPapelId(usuarioId, papelId);

        UsuarioPapel associacao = usuarioPapelRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Associação entre usuário e papel não encontrada"));

        usuarioPapelRepository.delete(associacao);
    }
}
