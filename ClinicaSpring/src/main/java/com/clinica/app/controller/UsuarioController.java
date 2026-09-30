package com.clinica.app.controller;

import com.clinica.app.entity.Usuario;
import com.clinica.app.repository.MedicoRepository;
import com.clinica.app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Optional;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private MedicoRepository medicoRepository;

    // Mostrar formulario de registro
    @GetMapping("/registro")
    public String formRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("medicos", medicoRepository.findAll());
        return "registro";
    }

    // Procesar registro (cuenta + perfil de paciente o médico)
    @PostMapping("/registro")
    public String guardarUsuario(@ModelAttribute Usuario usuario,
                                 @RequestParam String nombre,
                                 @RequestParam String apellido,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaNacimiento,
                                 @RequestParam(required = false) String cedula,
                                 @RequestParam(required = false) String especialidad,
                                 @RequestParam(required = false) String telefonoPac,
                                 @RequestParam(required = false) String telefonoMed,
                                 @RequestParam(required = false) String alergias,
                                 @RequestParam(required = false) String tipoSangre,
                                 @RequestParam(required = false) Integer medicoPreferencia,
                                 @RequestParam String confirmar,
                                 Model model,
                                 HttpSession session) {
        try {
            if (!usuario.getPasswordHash().equals(confirmar)) {
                throw new IllegalArgumentException("Las contraseñas no coinciden.");
            }
            String telefono = (telefonoMed != null && !telefonoMed.isBlank()) ? telefonoMed : telefonoPac;
            Usuario guardado = usuarioService.registrarConPerfil(usuario, nombre, apellido, fechaNacimiento, cedula, especialidad, telefono, alergias, tipoSangre);
            // Entrada directa: deja la sesión lista y manda a agendar con el médico elegido
            session.setAttribute("usuarioId", guardado.getId());
            session.setAttribute("correo", guardado.getCorreo());
            session.setAttribute("rolId", guardado.getRol().getId());
            session.setAttribute("rolNombre", guardado.getRol().getNombre());
            if (Integer.valueOf(2).equals(guardado.getRol().getId()) && medicoPreferencia != null) {
                return "redirect:/citas/agendar?medico=" + medicoPreferencia;
            }
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("medicos", medicoRepository.findAll());
            return "registro";
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            model.addAttribute("error", "Ese correo o cédula ya están registrados.");
            model.addAttribute("medicos", medicoRepository.findAll());
            return "registro";
        }
        return "redirect:/login?exito";
    }

    // Mostrar formulario de login
    @GetMapping("/login")
    public String formLogin() {
        return "login";
    }

    // Procesar login
    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo,
                                @RequestParam String password,
                                Model model,
                                HttpSession session) {
        boolean esValido = usuarioService.validarLogin(correo, password);
        if (esValido) {
            Optional<Usuario> userOpt = usuarioService.obtenerPorCorreo(correo);
            Usuario usuario = userOpt.get();
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("correo", usuario.getCorreo());
            session.setAttribute("rolId", usuario.getRol().getId());
            session.setAttribute("rolNombre", usuario.getRol().getNombre());
            return "redirect:/panel";
        } else {
            model.addAttribute("error", "Correo o contraseña incorrectos.");
            model.addAttribute("correo", correo);
            return "login";
        }
    }

    // Cerrar sesión
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}