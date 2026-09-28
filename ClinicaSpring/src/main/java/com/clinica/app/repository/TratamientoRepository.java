package com.clinica.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clinica.app.entity.Tratamiento;

public interface TratamientoRepository extends JpaRepository<Tratamiento, Integer>{
	List<Tratamiento> findByConsultaIdConsulta(Integer idConsulta);
}
