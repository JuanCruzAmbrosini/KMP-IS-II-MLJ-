package com.biblioteca.service;

import com.biblioteca.model.Rol;
import com.biblioteca.model.Usuario;
import com.biblioteca.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio de Usuario. Aca es donde se aplica la ENCRIPTACION de la clave:
 * nunca se guarda en texto plano, siempre pasa por BCryptPasswordEncoder.
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Alta publica desde el formulario de registracion -> siempre rol USUARIO
    public Usuario registrar(Usuario usuario) {
        if (usuarioRepository.existsByMail(usuario.getMail())) {
            throw new IllegalStateException("Ya existe un usuario registrado con ese mail");
        }
        usuario.setClave(passwordEncoder.encode(usuario.getClave()));
        usuario.setRol(Rol.USUARIO);
        usuario.setAlta(true);
        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorMail(String mail) {
        return usuarioRepository.findByMail(mail)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + mail));
    }

    // Cambio de clave: recibe la clave en texto plano y la vuelve a encriptar
    public void cambiarClave(String mail, String claveNueva) {
        Usuario usuario = buscarPorMail(mail);
        usuario.setClave(passwordEncoder.encode(claveNueva));
        usuarioRepository.save(usuario);
    }
}
