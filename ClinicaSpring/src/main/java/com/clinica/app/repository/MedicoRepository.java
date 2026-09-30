package com.clinica.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clinica.app.entity.Medico;

public interface MedicoRepository extends JpaRepository<Medico, Integer>{

    java.util.Optional<Medico> findByUsuarioIdUsuario(Integer idUsuario);

}
