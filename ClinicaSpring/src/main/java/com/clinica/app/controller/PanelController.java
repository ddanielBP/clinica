package com.clinica.app.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.clinica.app.entity.Cita;
import com.clinica.app.entity.ConsultaHistorial;
import com.clinica.app.entity.Medico;
import com.clinica.app.entity.Paciente;
import com.clinica.app.entity.Usuario;
import com.clinica.app.repository.CitaRepository;
import com.clinica.app.repository.ConsultaHistorialRepository;
import com.clinica.app.repository.MedicoRepository;
import com.clinica.app.repository.PacienteRepository;
import com.clinica.app.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class PanelController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private ConsultaHistorialRepository consultaRepository;

    @GetMapping("/panel")
    public String panel(HttpSession session, Model model) {
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null || Boolean.FALSE.equals(usuario.getActivo())) {
            session.invalidate();
            return "redirect:/login";
        }

        boolean esMedico = usuario.getRol() != null && Integer.valueOf(1).equals(usuario.getRol().getId());
        model.addAttribute("usuario", usuario);
        model.addAttribute("rolNombre", usuario.getRol() != null ? usuario.getRol().getNombre() : "");
        model.addAttribute("esMedico", esMedico);
        model.addAttribute("colores", Arrays.asList("#0d7d9c", "#6e2e4e", "#147a3e", "#9a6200"));

        LocalDateTime ahora = LocalDateTime.now();

        if (esMedico) {
            Medico medico = medicoRepository.findByUsuarioIdUsuario(usuarioId).orElse(null);
            if (medico == null) {
                return "redirect:/login";
            }
            List<Cita> citas = new ArrayList<>();
            for (Cita c : citaRepository.findByMedicoIdMedicoOrderByFechaHoraAsc(medico.getId())) {
                if (c.getFechaHora() != null && !c.getFechaHora().isBefore(ahora)) {
                    citas.add(c);
                }
            }
            List<Paciente> pacientes = citas.stream()
                    .map(Cita::getPaciente)
                    .distinct()
                    .toList();
            List<ConsultaHistorial> expedientes = consultaRepository.findByMedicoIdMedico(medico.getId());

            model.addAttribute("perfilNombre", medico.getNombre() + " " + medico.getApellido());
            model.addAttribute("perfilFolio", "Céd. " + medico.getCedula());
            model.addAttribute("perfil", medico);
            model.addAttribute("iniciales", iniciales(medico.getNombre(), medico.getApellido()));
            model.addAttribute("citas", citas);
            model.addAttribute("proximaCita", citas.isEmpty() ? null : citas.get(0));
            model.addAttribute("expedientes", expedientes.size() > 3 ? expedientes.subList(0, 3) : expedientes);
            model.addAttribute("totalExpedientes", expedientes.size());
            model.addAttribute("pacientes", pacientes);
        } else {
            Paciente paciente = pacienteRepository.findByUsuarioIdUsuario(usuarioId).orElse(null);
            if (paciente == null) {
                return "redirect:/login";
            }
            List<Cita> citas = new ArrayList<>();
            for (Cita c : citaRepository.findByPacienteIdPacienteOrderByFechaHoraAsc(paciente.getIdPaciente())) {
                if (c.getFechaHora() != null && !c.getFechaHora().isBefore(ahora)) {
                    citas.add(c);
                }
            }
            List<ConsultaHistorial> expedientes = consultaRepository.findByPacienteIdPaciente(paciente.getIdPaciente());
            Medico medicoAsignado = null;
            if (!citas.isEmpty()) {
                medicoAsignado = citas.get(0).getMedico();
            } else if (!expedientes.isEmpty()) {
                medicoAsignado = expedientes.get(0).getMedico();
            }

            model.addAttribute("perfilNombre", paciente.getNombre() + " " + paciente.getApellido());
            model.addAttribute("perfilFolio", "PAC-" + paciente.getIdPaciente());
            model.addAttribute("perfil", paciente);
            model.addAttribute("iniciales", iniciales(paciente.getNombre(), paciente.getApellido()));
            model.addAttribute("citas", citas);
            model.addAttribute("proximaCita", citas.isEmpty() ? null : citas.get(0));
            model.addAttribute("expedientes", expedientes.size() > 3 ? expedientes.subList(0, 3) : expedientes);
            model.addAttribute("totalExpedientes", expedientes.size());
            model.addAttribute("medicoAsignado", medicoAsignado);
        }

        return "panel";
    }

    private String iniciales(String nombre, String apellido) {
        String n = (nombre != null && !nombre.isEmpty()) ? nombre.substring(0, 1) : "";
        String a = (apellido != null && !apellido.isEmpty()) ? apellido.substring(0, 1) : "";
        return (n + a).toUpperCase();
    }
}
