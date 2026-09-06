package com.biblioteca.security;

import com.biblioteca.model.Usuario;
import com.biblioteca.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Puente entre Spring Security y nuestra entidad Usuario.
 * El "username" de Spring Security es el mail del Usuario.
 * El rol se expone con el prefijo ROLE_ que exige Spring Security (ROLE_ADMIN, ROLE_USUARIO).
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByMail(mail)
                .filter(Usuario::isAlta)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + mail));

        return User.builder()
                .username(usuario.getMail())
                .password(usuario.getClave()) // ya viene encriptada con BCrypt
                .authorities(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()))
                .build();
    }
}
