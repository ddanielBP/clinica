package com.clinica.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clinica.app.entity.Cita;

public interface CitaRepository extends JpaRepository<Cita, Integer>{

    java.util.List<Cita> findByMedicoIdMedicoOrderByFechaHoraAsc(Integer idMedico);

    java.util.List<Cita> findByMedicoIdMedicoOrderByFechaHoraDesc(Integer idMedico);

    java.util.List<Cita> findByPacienteIdPacienteOrderByFechaHoraAsc(Integer idPaciente);

    java.util.List<Cita> findByMedicoIdMedicoAndFechaHoraBetween(Integer idMedico,
            java.time.LocalDateTime desde, java.time.LocalDateTime hasta);

    boolean existsByMedicoIdMedicoAndFechaHora(Integer idMedico, java.time.LocalDateTime fechaHora);

}
