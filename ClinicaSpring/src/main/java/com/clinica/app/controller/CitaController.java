package com.clinica.app.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.clinica.app.entity.Cita;
import com.clinica.app.entity.ConsultaHistorial;
import com.clinica.app.entity.Medico;
import com.clinica.app.entity.Paciente;
import com.clinica.app.entity.Tratamiento;
import com.clinica.app.repository.CitaRepository;
import com.clinica.app.repository.ConsultaHistorialRepository;
import com.clinica.app.repository.MedicoRepository;
import com.clinica.app.repository.PacienteRepository;
import com.clinica.app.repository.TratamientoRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class CitaController {

    // Horario de consultorio: Lun–Vie, 09:00–13:30 cada 30 min
    public static final int HORA_INICIO = 9;
    public static final int HORA_FIN = 13;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ConsultaHistorialRepository consultaRepository;

    @Autowired
    private TratamientoRepository tratamientoRepository;

    // ---------------- Médico: bandeja de citas ----------------

    @GetMapping("/citas")
    public String bandeja(HttpSession session, Model model) {
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }
        Medico medico = medicoRepository.findByUsuarioIdUsuario(usuarioId).orElse(null);
        if (medico == null) {
            return "redirect:/panel";
        }
        List<Cita> pendientes = citaRepository.findByMedicoIdMedicoOrderByFechaHoraAsc(medico.getId())
                .stream()
                .filter(c -> "PENDIENTE".equals(c.getEstado()))
                .toList();
        List<Cita> historial = citaRepository.findByMedicoIdMedicoOrderByFechaHoraDesc(medico.getId())
                .stream()
                .filter(c -> !"PENDIENTE".equals(c.getEstado()))
                .toList();
        model.addAttribute("medico", medico);
        model.addAttribute("pendientes", pendientes);
        model.addAttribute("historial", historial);
        return "citas";
    }

    @PostMapping("/citas/{id}/aceptar")
    public String aceptar(@PathVariable Integer id, HttpSession session) {
        return cambiarEstado(id, session, "CONFIRMADA");
    }

    @PostMapping("/citas/{id}/rechazar")
    public String rechazar(@PathVariable Integer id, HttpSession session) {
        return cambiarEstado(id, session, "CANCELADA");
    }

    @PostMapping("/citas/{id}/cancelar")
    public String cancelar(@PathVariable Integer id, HttpSession session) {
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }
        Cita cita = citaRepository.findById(id).orElse(null);
        Paciente paciente = pacienteRepository.findByUsuarioIdUsuario(usuarioId).orElse(null);
        if (cita == null || paciente == null
                || !paciente.getIdPaciente().equals(cita.getPaciente().getIdPaciente())
                || (!"PENDIENTE".equals(cita.getEstado()) && !"CONFIRMADA".equals(cita.getEstado()))) {
            return "redirect:/panel";
        }
        cita.setEstado("CANCELADA");
        citaRepository.save(cita);
        return "redirect:/citas/" + id;
    }

    private String cambiarEstado(Integer id, HttpSession session, String estado) {
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }
        Medico medico = medicoRepository.findByUsuarioIdUsuario(usuarioId).orElse(null);
        Cita cita = citaRepository.findById(id).orElse(null);
        if (medico == null || cita == null
                || !medico.getId().equals(cita.getMedico().getId())
                || !"PENDIENTE".equals(cita.getEstado())) {
            return "redirect:/citas";
        }
        cita.setEstado(estado);
        citaRepository.save(cita);
        return "redirect:/citas";
    }

    // ---------------- Paciente: agendar ----------------

    @GetMapping("/citas/agendar")
    public String formAgendar(HttpSession session, Model model,
                              @RequestParam(required = false) Integer medico) {
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }
        Paciente paciente = pacienteRepository.findByUsuarioIdUsuario(usuarioId).orElse(null);
        if (paciente == null) {
            return "redirect:/panel";
        }
        List<Medico> medicos = medicoRepository.findAll();
        if (medicos.isEmpty()) {
            model.addAttribute("error", "Aún no hay médicos registrados.");
        }
        Integer preseleccionado = null;
        List<Cita> previas = citaRepository.findByPacienteIdPacienteOrderByFechaHoraAsc(paciente.getIdPaciente());
        if (medico != null && medicoRepository.existsById(medico)) {
            preseleccionado = medico;
        } else if (!previas.isEmpty()) {
            preseleccionado = previas.get(previas.size() - 1).getMedico().getId();
        } else if (!medicos.isEmpty()) {
            preseleccionado = medicos.get(0).getId();
        }
        model.addAttribute("medicos", medicos);
        model.addAttribute("preseleccionado", preseleccionado);
        return "cita-agendar";
    }

    @GetMapping("/api/citas/ocupadas")
    @ResponseBody
    public List<String> ocupadas(@RequestParam Integer medicoId,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return citaRepository.findByMedicoIdMedicoAndFechaHoraBetween(medicoId, desde.atStartOfDay(), hasta.atTime(23, 59))
                .stream()
                .filter(c -> !"CANCELADA".equals(c.getEstado()))
                .map(c -> c.getFechaHora().toString())
                .toList();
    }

    @PostMapping("/citas/agendar")
    public String agendar(HttpSession session,
                          @RequestParam Integer medicoId,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora,
                          @RequestParam String motivo,
                          Model model) {
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }
        Paciente paciente = pacienteRepository.findByUsuarioIdUsuario(usuarioId).orElse(null);
        Medico medico = medicoRepository.findById(medicoId).orElse(null);
        String error = validarSlot(medicoId, fechaHora);
        if (paciente == null || medico == null) {
            error = "Sesión o médico no válido.";
        } else if (motivo == null || motivo.isBlank()) {
            error = "Escribe el motivo de la cita.";
        }
        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("medicos", medicoRepository.findAll());
            model.addAttribute("preseleccionado", medicoId);
            return "cita-agendar";
        }
        Cita cita = new Cita(null, paciente, medico, fechaHora, motivo.strip(), "PENDIENTE");
        citaRepository.save(cita);
        return "redirect:/panel?cita=ok";
    }

    // ---------------- Detalle + atención ----------------

    @GetMapping("/citas/{id}")
    public String detalle(@PathVariable Integer id, HttpSession session, Model model) {
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }
        Cita cita = citaRepository.findById(id).orElse(null);
        if (cita == null || !esDuenio(usuarioId, cita)) {
            return "redirect:/panel";
        }
        boolean esMedico = medicoRepository.findByUsuarioIdUsuario(usuarioId)
                .map(m -> m.getId().equals(cita.getMedico().getId()))
                .orElse(false);
        ConsultaHistorial consulta = consultaRepository.findByCitaIdCita(id).orElse(null);
        model.addAttribute("cita", cita);
        model.addAttribute("esMedico", esMedico);
        model.addAttribute("consulta", consulta);
        model.addAttribute("puedeAtender", esMedico && "CONFIRMADA".equals(cita.getEstado()) && consulta == null);
        if (consulta != null) {
            model.addAttribute("tratamientos", tratamientoRepository.findByConsultaIdConsulta(consulta.getIdConsulta()));
        }
        return "cita-detalle";
    }

    @PostMapping("/citas/{id}/atender")
    public String atender(@PathVariable Integer id,
                          @RequestParam String diagnostico,
                          @RequestParam(required = false) String signosVitales,
                          @RequestParam(required = false) String notasMedicas,
                          @RequestParam(required = false) String medicamento,
                          @RequestParam(required = false) String dosis,
                          @RequestParam(required = false) Integer duracionDias,
                          @RequestParam(required = false) String indicaciones,
                          HttpSession session, Model model) {
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");
        if (usuarioId == null) {
            return "redirect:/login";
        }
        Cita cita = citaRepository.findById(id).orElse(null);
        Medico medico = medicoRepository.findByUsuarioIdUsuario(usuarioId).orElse(null);
        if (cita == null || medico == null
                || !medico.getId().equals(cita.getMedico().getId())
                || !"CONFIRMADA".equals(cita.getEstado())
                || consultaRepository.findByCitaIdCita(id).isPresent()
                || diagnostico == null || diagnostico.isBlank()) {
            return "redirect:/citas/" + id;
        }
        ConsultaHistorial consulta = new ConsultaHistorial();
        consulta.setCita(cita);
        consulta.setPaciente(cita.getPaciente());
        consulta.setMedico(medico);
        consulta.setMotivoConsulta(cita.getMotivo());
        consulta.setDiagnostco(diagnostico.strip());
        consulta.setSignosVitales(vacioANull(signosVitales));
        consulta.setNotasMedicas(vacioANull(notasMedicas));
        consultaRepository.save(consulta);

        if (medicamento != null && !medicamento.isBlank() && dosis != null && !dosis.isBlank()) {
            Tratamiento tto = new Tratamiento();
            tto.setConsulta(consulta);
            tto.setMedicamentoOProcedimiento(medicamento.strip());
            tto.setDosisInstrucciones(dosis.strip());
            tto.setDuracionDias(duracionDias);
            tto.setIndicacionesAdicionales(vacioANull(indicaciones));
            tratamientoRepository.save(tto);
        }
        cita.setEstado("ATENDIDA");
        citaRepository.save(cita);
        return "redirect:/citas/" + id;
    }

    private boolean esDuenio(Integer usuarioId, Cita cita) {
        return medicoRepository.findByUsuarioIdUsuario(usuarioId)
                .map(m -> m.getId().equals(cita.getMedico().getId()))
                .orElse(false)
            || pacienteRepository.findByUsuarioIdUsuario(usuarioId)
                .map(p -> p.getIdPaciente().equals(cita.getPaciente().getIdPaciente()))
                .orElse(false);
    }

    private String vacioANull(String s) {
        return (s == null || s.isBlank()) ? null : s.strip();
    }

    private String validarSlot(Integer medicoId, LocalDateTime fechaHora) {
        if (fechaHora == null || fechaHora.isBefore(LocalDateTime.now())) {
            return "Elige una fecha y hora futura.";
        }
        DayOfWeek dia = fechaHora.getDayOfWeek();
        if (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY) {
            return "El consultorio atiende de lunes a viernes.";
        }
        LocalTime hora = fechaHora.toLocalTime();
        if (hora.getHour() < HORA_INICIO || hora.getHour() > HORA_FIN
                || (hora.getMinute() != 0 && hora.getMinute() != 30)) {
            return "Elige un horario de 09:00 a 13:30 en bloques de 30 minutos.";
        }
        if (citaRepository.existsByMedicoIdMedicoAndFechaHora(medicoId, fechaHora)) {
            return "Ese horario ya está ocupado. Elige otro.";
        }
        return null;
    }
}
