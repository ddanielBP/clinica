package com.clinica.app.service;

import com.clinica.app.entity.Rol;
import com.clinica.app.entity.Medico;
import com.clinica.app.entity.Paciente;
import com.clinica.app.entity.Usuario;
import com.clinica.app.repository.MedicoRepository;
import com.clinica.app.repository.PacienteRepository;
import com.clinica.app.repository.RolRepository;
import com.clinica.app.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 1. Registro: Encripta con salt aleatorio antes de guardar
    public Usuario registrar(Usuario usuario) {
        String hash = passwordEncoder.encode(usuario.getPasswordHash());
        usuario.setPasswordHash(hash); // Reemplaza texto plano por el hash seguro
        usuario.setFechaCreacion(java.time.LocalDateTime.now());
        
        if (usuario.getRol() == null) {
            Rol rolPorDefecto = new Rol();
            rolPorDefecto.setId(2); // PACIENTE por defecto
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

    public Optional<Usuario> obtenerPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    public boolean correoExiste(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    // 3. Registro con perfil: crea Usuario + Paciente o Medico en una transacción.
    // Convención de roles: 1 = MEDICO, 2 = PACIENTE.
    @Transactional
    public Usuario registrarConPerfil(Usuario usuario, String nombre, String apellido,
                                      LocalDate fechaNacimiento, String cedula, String especialidad, String telefono,
                                      String alergias, String tipoSangre) {
        if (correoExiste(usuario.getCorreo())) {
            throw new IllegalArgumentException("Ese correo ya está registrado.");
        }
        Integer rolId = (usuario.getRol() != null && usuario.getRol().getId() != null)
                ? usuario.getRol().getId() : 2;
        if (Integer.valueOf(2).equals(rolId) && fechaNacimiento == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria.");
        }
        if (Integer.valueOf(1).equals(rolId)
                && (cedula == null || cedula.isBlank() || especialidad == null || especialidad.isBlank())) {
            throw new IllegalArgumentException("La cédula y la especialidad son obligatorias.");
        }
        Rol rol = rolRepository.findById(rolId)
                .orElseThrow(() -> new IllegalArgumentException("Rol no válido."));
        usuario.setRol(rol);
        usuario.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));
        usuario.setFechaCreacion(java.time.LocalDateTime.now());
        usuario.setActivo(true);
        Usuario guardado = usuarioRepository.save(usuario);

        if (Integer.valueOf(1).equals(rolId)) {
            Medico medico = new Medico(null, guardado, nombre, apellido, cedula, especialidad, telefono);
            medicoRepository.save(medico);
        } else {
            Paciente paciente = new Paciente(null, guardado, nombre, apellido, fechaNacimiento, telefono, alergias, tipoSangre);
            pacienteRepository.save(paciente);
        }
        return guardado;
    }
}