
package com.rede.rede_restaurantes.config;

import com.rede.rede_restaurantes.entity.Usuario;
import com.rede.rede_restaurantes.entity.Papel;
import com.rede.rede_restaurantes.entity.UsuarioPapel;
import com.rede.rede_restaurantes.entity.UsuarioPapelId;

import com.rede.rede_restaurantes.repository.UsuarioRepository;
import com.rede.rede_restaurantes.repository.PapelRepository;
import com.rede.rede_restaurantes.repository.UsuarioPapelRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner inicializarDados(
            UsuarioRepository usuarioRepository,
            PapelRepository papelRepository,
            UsuarioPapelRepository usuarioPapelRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // Criar ou buscar papel ADMIN
            Papel admin = papelRepository.findByNome("ADMIN")
                    .orElseGet(() -> {
                        Papel novoPapel = new Papel();
                        novoPapel.setNome("ADMIN");
                        return papelRepository.save(novoPapel);
                    });

            // Criar ou buscar administrador
            Usuario administrador = usuarioRepository
                    .findByEmail("teste@rede.com")
                    .orElseGet(() -> {
                        Usuario novoUsuario = new Usuario();
                        novoUsuario.setNome("Administrador");
                        novoUsuario.setEmail("teste@rede.com");
                        novoUsuario.setSenhaHash(
                                passwordEncoder.encode("Teste@123")
                        );
                        novoUsuario.setAtivo(true);
                        return usuarioRepository.save(novoUsuario);
                    });

            UsuarioPapelId idAdmin = new UsuarioPapelId(
                    administrador.getId(),
                    admin.getId()
            );

            if (!usuarioPapelRepository.existsById(idAdmin)) {
                usuarioPapelRepository.save(
                        new UsuarioPapel(administrador, admin)
                );
            }

            // Criar ou buscar papel USER
            Papel user = papelRepository.findByNome("USER")
                    .orElseGet(() -> {
                        Papel novoPapel = new Papel();
                        novoPapel.setNome("USER");
                        return papelRepository.save(novoPapel);
                    });

            // Criar ou buscar usuário comum
            Usuario usuarioComum = usuarioRepository
                    .findByEmail("usuario@rede.com")
                    .orElseGet(() -> {
                        Usuario novoUsuario = new Usuario();
                        novoUsuario.setNome("Usuário Comum");
                        novoUsuario.setEmail("usuario@rede.com");
                        novoUsuario.setSenhaHash(
                                passwordEncoder.encode("Usuario@123")
                        );
                        novoUsuario.setAtivo(true);
                        return usuarioRepository.save(novoUsuario);
                    });

            UsuarioPapelId idUser = new UsuarioPapelId(
                    usuarioComum.getId(),
                    user.getId()
            );

            if (!usuarioPapelRepository.existsById(idUser)) {
                usuarioPapelRepository.save(
                        new UsuarioPapel(usuarioComum, user)
                );
            }
        };
    }
}
