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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import um.tesoreria.core.kotlin.model.Auditable;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(name = "contratofactura", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "cfa_con_clave", "cfa_tco_id", "cfa_prefijo", "cfa_nrocomprob" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ContratoFactura extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1316742193504426801L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cfa_id")
	private Long contratofacturaId;

	@Column(name = "cfa_con_clave")
	private Long contratoId;

	@Column(name = "cfa_tco_id")
	private Integer comprobanteId;

	@Column(name = "cfa_prefijo")
	private Integer prefijo = 0;

	@Column(name = "cfa_nrocomprob")
	private Long numerocomprobante;

	@Column(name = "cfa_fecha")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fecha;

	@Column(name = "cfa_importe")
	private BigDecimal importe = BigDecimal.ZERO;

	@Column(name = "cfa_pendiente")
	private Byte pendiente = 0;

	@Column(name = "cfa_excluido")
	private Byte excluido = 0;

	@Column(name = "cfa_acreditacion")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime acreditacion;

	@Column(name = "cfa_envio")
	private Integer envio = 0;

	@Column(name = "cfa_cbu")
	private String cbu = "";

}
