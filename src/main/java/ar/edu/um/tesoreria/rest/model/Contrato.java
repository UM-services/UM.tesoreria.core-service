/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import ar.edu.um.tesoreria.rest.kotlin.model.Contratado;
import ar.edu.um.tesoreria.rest.kotlin.model.Facultad;
import ar.edu.um.tesoreria.rest.kotlin.model.Geografica;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

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
@Table
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Contrato extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6511834189234951853L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long contratoId;

	private Long contratadoId;

	@Column(name = "con_per_id")
	private BigDecimal personaId;

	@Column(name = "con_doc_id")
	private Integer documentoId;

	@Column(name = "con_desde")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime desde;

	@Column(name = "con_fac_id")
	private Integer facultadId;

	@Column(name = "con_pla_id")
	private Integer planId;

	@Column(name = "con_mat_id")
	private String materiaId;

	@Column(name = "con_geo_id")
	private Integer geograficaId;

	@Column(name = "con_cam_id")
	private Integer cargoMateriaId;

	@Column(name = "con_primervenc")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime primerVencimiento;

	@Column(name = "con_cargo")
	private String cargo = "";

	private BigDecimal montoFijo = BigDecimal.ZERO;

	@Column(name = "con_canonmensual")
	private BigDecimal canonMensual = BigDecimal.ZERO;

	@Column(name = "con_canonmensualsa")
	private BigDecimal canonMensualSinAjuste = BigDecimal.ZERO;

	@Column(name = "con_hasta")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime hasta;

	@Column(name = "con_canonmensualletras")
	private String canonMensualLetras = "";

	@Column(name = "con_canontotal")
	private BigDecimal canonTotal = BigDecimal.ZERO;

	@Column(name = "con_canontotalletras")
	private String canonTotalLetras = "";

	@Column(name = "con_meses")
	private Integer meses = 0;

	@Column(name = "con_mesesletras")
	private String mesesLetras = "";

	@Column(name = "con_ajuste")
	private Byte ajuste = 0;

	@OneToOne
	@JoinColumn(name = "contratadoId", insertable = false, updatable = false)
	private Contratado contratado;

	@OneToOne
	@JoinColumn(name = "con_fac_id", insertable = false, updatable = false)
	private Facultad facultad;

	@OneToOne
	@JoinColumn(name = "con_geo_id", insertable = false, updatable = false)
	private Geografica geografica;

}
