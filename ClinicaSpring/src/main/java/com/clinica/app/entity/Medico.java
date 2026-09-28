package com.clinica.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "medico")
public class Medico {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_medico")
	private Integer idMedico;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario", nullable = false, unique = true)
	private Usuario usuario;
	
	@Column(name = "nombre", length = 100, nullable = false)
	private String nombre;
	
	@Column(name = "apellido", length = 100, nullable = false)
	private String apellido;
	
	@Column(name = "cedula_profesional", length = 50, nullable = false, unique = true)
	private String cedula;
	
	@Column(name = "especialidad", length = 100, nullable = false)
	private String especialidad;
	
	@Column(name = "telefono", length = 20)
	private String telefono;

	public Medico() {
		super();
	}

	public Medico(Integer id, Usuario usuario, String nombre, String apellido, String cedula, String especialidad,
			String telefono) {
		super();
		this.idMedico = id;
		this.usuario = usuario;
		this.nombre = nombre;
		this.apellido = apellido;
		this.cedula = cedula;
		this.especialidad = especialidad;
		this.telefono = telefono;
	}

	public Integer getId() {
		return idMedico;
	}

	public void setId(Integer id) {
		this.idMedico = id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getEspecialidad() {
		return especialidad;
	}

	public void setEspecialidad(String especialidad) {
		this.especialidad = especialidad;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

}
