
package com.rede.rede_restaurantes.service;

import com.rede.rede_restaurantes.entity.Usuario;
import com.rede.rede_restaurantes.entity.UsuarioPapel;
import com.rede.rede_restaurantes.repository.UsuarioRepository;
import com.rede.rede_restaurantes.repository.UsuarioPapelRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioPapelRepository usuarioPapelRepository;

    public UsuarioDetailsService(
            UsuarioRepository usuarioRepository,
            UsuarioPapelRepository usuarioPapelRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioPapelRepository = usuarioPapelRepository;
    }

    @Override
public UserDetails loadUserByUsername(String email)
        throws UsernameNotFoundException {

    Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(
                    "Usuário não encontrado"));

    List<UsuarioPapel> papeis =
            usuarioPapelRepository.findByUsuarioEmail(email);

    List<SimpleGrantedAuthority> authorities = papeis.stream()
            .map(usuarioPapel -> new SimpleGrantedAuthority(
                    "ROLE_" + usuarioPapel.getPapel().getNome()))
            .toList();


    return User.builder()
            .username(usuario.getEmail())
            .password(usuario.getSenhaHash())
            .disabled(!Boolean.TRUE.equals(usuario.getAtivo()))
            .authorities(authorities)
            .build();
}
}
