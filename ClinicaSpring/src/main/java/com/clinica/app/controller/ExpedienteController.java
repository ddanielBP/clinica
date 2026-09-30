package com.clinica.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.clinica.app.entity.ConsultaHistorial;
import com.clinica.app.entity.Medico;
import com.clinica.app.entity.Paciente;
import com.clinica.app.entity.Usuario;
import com.clinica.app.repository.ConsultaHistorialRepository;
import com.clinica.app.repository.MedicoRepository;
import com.clinica.app.repository.PacienteRepository;
import com.clinica.app.repository.TratamientoRepository;
import com.clinica.app.repository.UsuarioRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class ExpedienteController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ConsultaHistorialRepository consultaRepository;

    @Autowired
    private TratamientoRepository tratamientoRepository;

    @GetMapping("/expedientes")
    public String lista(HttpSession session, Model model) {
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) {
            session.invalidate();
            return "redirect:/login";
        }
        boolean esMedico = usuario.getRol() != null && Integer.valueOf(1).equals(usuario.getRol().getId());
        List<ConsultaHistorial> lista;
        if (esMedico) {
            Medico medico = medicoRepository.findByUsuarioIdUsuario(usuarioId).orElse(null);
            if (medico == null) {
                return "redirect:/panel";
            }
            lista = consultaRepository.findByMedicoIdMedicoOrderByFechaConsultaDesc(medico.getId());
        } else {
            Paciente paciente = pacienteRepository.findByUsuarioIdUsuario(usuarioId).orElse(null);
            if (paciente == null) {
                return "redirect:/panel";
            }
            lista = consultaRepository.findByPacienteIdPacienteOrderByFechaConsultaDesc(paciente.getIdPaciente());
        }
        model.addAttribute("lista", lista);
        model.addAttribute("esMedico", esMedico);
        return "expedientes";
    }

    @GetMapping("/expedientes/{id}")
    public String detalle(@PathVariable Integer id, HttpSession session, Model model) {
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        ConsultaHistorial expediente = consultaRepository.findById(id).orElse(null);
        if (usuario == null || expediente == null || !tieneAcceso(usuario, expediente)) {
            return "redirect:/panel";
        }
        model.addAttribute("expediente", expediente);
        model.addAttribute("tratamientos", tratamientoRepository.findByConsultaIdConsulta(id));
        return "expediente-detalle";
    }

    private boolean tieneAcceso(Usuario usuario, ConsultaHistorial expediente) {
        if (usuario.getRol() != null && Integer.valueOf(1).equals(usuario.getRol().getId())) {
            Medico medico = medicoRepository.findByUsuarioIdUsuario(usuario.getId()).orElse(null);
            return medico != null && medico.getId().equals(expediente.getMedico().getId());
        }
        Paciente paciente = pacienteRepository.findByUsuarioIdUsuario(usuario.getId()).orElse(null);
        return paciente != null && paciente.getIdPaciente().equals(expediente.getPaciente().getIdPaciente());
    }
}
