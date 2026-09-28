package com.clinica.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clinica.app.entity.ConsultaHistorial;

public interface ConsultaHistorialRepository extends JpaRepository<ConsultaHistorial, Integer>{
	List<ConsultaHistorial> findByPacienteIdPaciente(Integer idPaciente);
}
