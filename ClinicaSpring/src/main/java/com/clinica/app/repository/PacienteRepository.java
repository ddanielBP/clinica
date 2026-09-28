package com.clinica.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clinica.app.entity.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Integer>{
	
}
