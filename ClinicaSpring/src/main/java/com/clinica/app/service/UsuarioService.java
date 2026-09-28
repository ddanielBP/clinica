package com.clinica.app.service;

import com.clinica.app.entity.Rol;
import com.clinica.app.entity.Usuario;
import com.clinica.app.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. Registro: Encripta con salt aleatorio antes de guardar
    public Usuario registrar(Usuario usuario) {
        String hash = passwordEncoder.encode(usuario.getPasswordHash());
        usuario.setPasswordHash(hash); // Reemplaza texto plano por el hash seguro
        usuario.setFechaCreacion(java.time.LocalDateTime.now());
        
        if (usuario.getRol() == null) {
            Rol rolPorDefecto = new Rol();
            rolPorDefecto.setId(3); // 
            usuario.setRol(rolPorDefecto);
        }
        
        return usuarioRepository.save(usuario);
    }

    // 2. Login: Compara la contraseña en texto plano con el hash de la BD
    public boolean validarLogin(String correo, String rawPassword) {
        Optional<Usuario> userOpt = usuarioRepository.findByCorreo(correo);
        if (userOpt.isPresent()) {
            Usuario usuario = userOpt.get();
            // passwordEncoder.matches() se encarga de extraer la sal del hash y comparar
            return passwordEncoder.matches(rawPassword, usuario.getPasswordHash());
        }
        return false;
    }
}