package com.clinica.app.controller;

import com.clinica.app.entity.Usuario;
import com.clinica.app.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Mostrar formulario de registro
    @GetMapping("/registro")
    public String formRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    // Procesar registro
    @PostMapping("/registro")
    public String guardarUsuario(@ModelAttribute Usuario usuario) {
        usuarioService.registrar(usuario);
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
                                Model model) {
        boolean esValido = usuarioService.validarLogin(correo, password);
        if (esValido) {
            model.addAttribute("mensaje", "¡Autenticación exitosa! Credenciales validadas con BCrypt.");
            return "bienvenido";
        } else {
            model.addAttribute("error", "Correo o contraseña incorrectos.");
            return "login";
        }
    }
}