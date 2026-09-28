package com.clinica.app.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "consulta_historial")
public class ConsultaHistorial {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_consulta")
	private Integer idConsulta;
	
	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cita", unique = true)
	private Cita cita;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_paciente", nullable = false)
	private Paciente paciente;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_medico", nullable = false)
	private Medico medico;
	
	@Column(name = "fecha_consulta", nullable = false, insertable = false, updatable = false)
	private LocalDateTime fechaConsulta;
	
	@Column(name = "motivo_consulta", length = 255, nullable = false)
	private String motivoConsulta;
	
	@Column(name = "diagnostico", columnDefinition = "TEXT", nullable = false)
	private String diagnostco;
	
	@Column(name = "signos_vitales", columnDefinition = "TEXT")
	private String signosVitales;
	
	@Column(name = "notas_medicas", columnDefinition = "TEXT")
	private String notasMedicas;
	
	@OneToMany(mappedBy = "consulta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tratamiento> tratamientos = new ArrayList<>();

	public ConsultaHistorial() {
		super();
	}

	public ConsultaHistorial(Integer idConsulta, Cita cita, Paciente paciente, Medico medico,
			LocalDateTime fechaConsulta, String motivoConsulta, String diagnostco, String signosVitales,
			String notasMedicas, List<Tratamiento> tratamientos) {
		super();
		this.idConsulta = idConsulta;
		this.cita = cita;
		this.paciente = paciente;
		this.medico = medico;
		this.fechaConsulta = fechaConsulta;
		this.motivoConsulta = motivoConsulta;
		this.diagnostco = diagnostco;
		this.signosVitales = signosVitales;
		this.notasMedicas = notasMedicas;
		this.tratamientos = tratamientos;
	}

	public Integer getIdConsulta() {
		return idConsulta;
	}

	public void setIdConsulta(Integer idConsulta) {
		this.idConsulta = idConsulta;
	}

	public Cita getCita() {
		return cita;
	}

	public void setCita(Cita cita) {
		this.cita = cita;
	}

	public Paciente getPaciente() {
		return paciente;
	}

	public void setPaciente(Paciente paciente) {
		this.paciente = paciente;
	}

	public Medico getMedico() {
		return medico;
	}

	public void setMedico(Medico medico) {
		this.medico = medico;
	}

	public LocalDateTime getFechaConsulta() {
		return fechaConsulta;
	}

	public void setFechaConsulta(LocalDateTime fechaConsulta) {
		this.fechaConsulta = fechaConsulta;
	}

	public String getMotivoConsulta() {
		return motivoConsulta;
	}

	public void setMotivoConsulta(String motivoConsulta) {
		this.motivoConsulta = motivoConsulta;
	}

	public String getDiagnostco() {
		return diagnostco;
	}

	public void setDiagnostco(String diagnostco) {
		this.diagnostco = diagnostco;
	}

	public String getSignosVitales() {
		return signosVitales;
	}

	public void setSignosVitales(String signosVitales) {
		this.signosVitales = signosVitales;
	}

	public String getNotasMedicas() {
		return notasMedicas;
	}

	public void setNotasMedicas(String notasMedicas) {
		this.notasMedicas = notasMedicas;
	}

	public List<Tratamiento> getTratamientos() {
		return tratamientos;
	}

	public void setTratamientos(List<Tratamiento> tratamientos) {
		this.tratamientos = tratamientos;
	}
	
}
