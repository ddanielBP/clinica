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
	
}
