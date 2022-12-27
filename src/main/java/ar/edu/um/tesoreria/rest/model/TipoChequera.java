package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class TipoChequera extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2545018779611750101L;

	@Id
	@Column(name = "tch_id")
	private Integer tipoChequeraId;

	@Column(name = "tch_nombre")
	private String nombre = "";

	@Column(name = "tch_prefijo")
	private String prefijo = "";

	@Column(name = "tch_geo_id")
	private Integer geograficaId = 1;

	@Column(name = "tch_cch_id")
	private Integer claseChequeraId = 2;

	@Column(name = "tch_imprimir")
	private Byte imprimir = 0;

	@Column(name = "tch_contado")
	private Byte contado = 0;

	@Column(name = "multiple")
	private Byte multiple = 0;

	@OneToOne(optional = true)
	@JoinColumn(name = "tch_geo_id", insertable = false, updatable = false)
	private Geografica geografica;

	@OneToOne(optional = true)
	@JoinColumn(name = "tch_cch_id", insertable = false, updatable = false)
	private ClaseChequera claseChequera;

}
