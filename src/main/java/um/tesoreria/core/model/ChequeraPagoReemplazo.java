/**
 * 
 */
package um.tesoreria.core.model;

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

import lombok.Data;
import lombok.EqualsAndHashCode;
import um.tesoreria.core.kotlin.model.Auditable;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "cpr_fac_id", "cpr_tch_id",
		"cpr_chs_id", "cpr_pro_id", "cpr_alt_id", "cpr_cuo_id", "cpr_orden" }) })
@EqualsAndHashCode(callSuper = false)
public class ChequeraPagoReemplazo extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -8330381928415301696L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "clave")
	private Long chequerapagoId;

	@Column(name = "cpr_fac_id")
	private Integer facultadId;

	@Column(name = "cpr_tch_id")
	private Integer tipochequeraId;

	@Column(name = "cpr_chs_id")
	private Long chequeraserieId;

	@Column(name = "cpr_pro_id")
	private Integer productoId;

	@Column(name = "cpr_alt_id")
	private Integer alternativaId;

	@Column(name = "cpr_cuo_id")
	private Integer cuotaId;

	@Column(name = "cpr_orden")
	private Integer orden;
	
	@Column(name = "cpr_mes")
	private Integer mes = 0;
	
	@Column(name = "cpr_anio")
	private Integer anho = 0;
	
	@Column(name = "cpr_fecha")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fecha;
	
	@Column(name = "cpr_acred")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime acreditacion;
	
	@Column(name = "cpr_importe")
	private BigDecimal importe = new BigDecimal(0);

	@Column(name = "cpr_path")
	private String path = "";

	@Column(name = "cpr_archivo")
	private String archivo = "";

	@Column(name = "cpr_observaciones")
	private String observaciones = "";

	@Column(name = "cpr_arb_id")
	private Long archivobancoId;

	@Column(name = "cpr_arb_id_acred")
	private Long archivobancoIdacreditacion;

	private Integer verificador = 0;

	@Column(name = "cpr_tpa_id")
	private Integer tipopagoId;

}
