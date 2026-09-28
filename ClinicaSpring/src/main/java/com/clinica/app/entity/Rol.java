package com.clinica.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rol")
public class Rol {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_rol")
	private Integer idRol;
	
	@Column(name = "nombre", length = 50, nullable = false, unique = true)
	private String nombre;

	public Rol() {
		super();
	}

	public Rol(Integer id, String nombre) {
		super();
		this.idRol = id;
		this.nombre = nombre;
	}

	public Integer getId() {
		return idRol;
	}

	public void setId(Integer id) {
		this.idRol = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
}
