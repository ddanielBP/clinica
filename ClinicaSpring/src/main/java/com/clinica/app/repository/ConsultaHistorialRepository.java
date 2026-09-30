package com.clinica.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clinica.app.entity.ConsultaHistorial;

public interface ConsultaHistorialRepository extends JpaRepository<ConsultaHistorial, Integer>{
	List<ConsultaHistorial> findByPacienteIdPaciente(Integer idPaciente);

	List<ConsultaHistorial> findByMedicoIdMedico(Integer idMedico);

	List<ConsultaHistorial> findByMedicoIdMedicoOrderByFechaConsultaDesc(Integer idMedico);

	List<ConsultaHistorial> findByPacienteIdPacienteOrderByFechaConsultaDesc(Integer idPaciente);

	java.util.Optional<ConsultaHistorial> findByCitaIdCita(Integer idCita);
}
