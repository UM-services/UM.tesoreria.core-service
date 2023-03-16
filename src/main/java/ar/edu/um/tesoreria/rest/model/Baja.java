/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import ar.edu.um.tesoreria.rest.model.kotlin.Persona;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "baj_fac_id", "baj_tch_id", "baj_chs_id" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Baja extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1516167638446918190L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long bajaId;

	@Column(name = "baj_fac_id")
	private Integer facultadId;

	@Column(name = "baj_tch_id")
	private Integer tipoChequeraId;

	@Column(name = "baj_chs_id")
	private Long chequeraSerieId;
	
	private Long chequeraId;

	@Column(name = "baj_lec_id")
	private Integer lectivoId;

	@Column(name = "baj_per_id")
	private BigDecimal personaId;

	@Column(name = "baj_doc_id")
	private Integer documentoId;

	@Column(name = "baj_fecha")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fecha;

	@Column(name = "baj_observaciones")
	private String observaciones;

	private Byte egresado = 0;

	@OneToOne(optional = true)
	@JoinColumn(name = "baj_fac_id", insertable = false, updatable = false)
	private Facultad facultad;

	@OneToOne(optional = true)
	@JoinColumn(name = "baj_tch_id", insertable = false, updatable = false)
	private TipoChequera tipoChequera;

	@OneToOne(optional = true)
	@JoinColumn(name = "baj_lec_id", insertable = false, updatable = false)
	private Lectivo lectivo;

	@OneToOne(optional = true)
	@JoinColumns({
			@JoinColumn(name = "baj_per_id", referencedColumnName = "per_id", insertable = false, updatable = false),
			@JoinColumn(name = "baj_doc_id", referencedColumnName = "per_doc_id", insertable = false, updatable = false) })
	private Persona persona;

	@OneToOne(optional = true)
	@JoinColumn(name = "chequeraId", insertable = false, updatable = false)
	private ChequeraSerie chequeraSerie;

}
