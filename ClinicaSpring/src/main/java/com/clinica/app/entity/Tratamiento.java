package com.clinica.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tratamiento")
public class Tratamiento {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tratamiento")
    private Integer idTratamiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_consulta", nullable = false)
    private ConsultaHistorial consulta;

    @Column(name = "medicamento_o_procedimiento", nullable = false, length = 150)
    private String medicamentoOProcedimiento;

    @Column(name = "dosis_instrucciones", nullable = false, columnDefinition = "TEXT")
    private String dosisInstrucciones;

    @Column(name = "duracion_dias")
    private Integer duracionDias;

    @Column(name = "indicaciones_adicionales", columnDefinition = "TEXT")
    private String indicacionesAdicionales;

    public Tratamiento() {
    }

    public Integer getIdTratamiento() {
        return idTratamiento;
    }

    public void setIdTratamiento(Integer idTratamiento) {
        this.idTratamiento = idTratamiento;
    }

    public ConsultaHistorial getConsulta() {
        return consulta;
    }

    public void setConsulta(ConsultaHistorial consulta) {
        this.consulta = consulta;
    }

    public String getMedicamentoOProcedimiento() {
        return medicamentoOProcedimiento;
    }

    public void setMedicamentoOProcedimiento(String medicamentoOProcedimiento) {
        this.medicamentoOProcedimiento = medicamentoOProcedimiento;
    }

    public String getDosisInstrucciones() {
        return dosisInstrucciones;
    }

    public void setDosisInstrucciones(String dosisInstrucciones) {
        this.dosisInstrucciones = dosisInstrucciones;
    }

    public Integer getDuracionDias() {
        return duracionDias;
    }

    public void setDuracionDias(Integer duracionDias) {
        this.duracionDias = duracionDias;
    }

    public String getIndicacionesAdicionales() {
        return indicacionesAdicionales;
    }

    public void setIndicacionesAdicionales(String indicacionesAdicionales) {
        this.indicacionesAdicionales = indicacionesAdicionales;
    }

}
