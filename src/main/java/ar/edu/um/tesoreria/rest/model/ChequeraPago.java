/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "chp_fac_id", "chp_tch_id",
		"chp_chs_id", "chp_pro_id", "chp_alt_id", "chp_cuo_id", "chp_orden" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ChequeraPago extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -6727715085277715816L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "clave")
	private Long chequeraPagoId;

	@Column(name = "chp_fac_id")
	private Integer facultadId;

	@Column(name = "chp_tch_id")
	private Integer tipoChequeraId;

	@Column(name = "chp_chs_id")
	private Long chequeraSerieId;

	@Column(name = "chp_pro_id")
	private Integer productoId;

	@Column(name = "chp_alt_id")
	private Integer alternativaId;

	@Column(name = "chp_cuo_id")
	private Integer cuotaId;

	@Column(name = "chp_orden")
	private Integer orden;

	@Column(name = "chp_mes")
	private Integer mes = 0;

	@Column(name = "chp_anio")
	private Integer anho = 0;

	@Column(name = "chp_fecha")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fecha;

	@Column(name = "chp_acred")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime acreditacion;

	@Column(name = "chp_importe")
	private BigDecimal importe = BigDecimal.ZERO;

	@Column(name = "chp_path")
	private String path = "";

	@Column(name = "chp_archivo")
	private String archivo = "";

	@Column(name = "chp_observaciones")
	private String observaciones = "";

	@Column(name = "chp_arb_id")
	private Long archivoBancoId;

	@Column(name = "chp_arb_id_acred")
	private Long archivoBancoIdAcreditacion;

	private Integer verificador = 0;

	@Column(name = "chp_tpa_id")
	private Integer tipoPagoId;

	public String getCuotaKey() {
		return this.facultadId + "." + this.tipoChequeraId + "." + this.chequeraSerieId + "." + this.productoId + "."
				+ this.alternativaId + "." + this.cuotaId;
	}

}
