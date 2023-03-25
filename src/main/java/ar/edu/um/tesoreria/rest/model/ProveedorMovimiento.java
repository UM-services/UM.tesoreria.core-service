/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import ar.edu.um.tesoreria.rest.kotlin.model.Comprobante;
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
@Table(name = "movprov")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorMovimiento extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9055200503520065996L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mvp_id")
	private Long proveedorMovimientoId;

	@Column(name = "mvp_prv_id")
	private Integer proveedorId;

	@Column(name = "mvp_nombrebeneficiario")
	private String nombreBeneficiario = "";

	@Column(name = "mvp_tco_id")
	private Integer comprobanteId;

	@Column(name = "mvp_fechacomprob")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fechaComprobante;

	@Column(name = "mvp_fechavenc")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fechaVencimiento;

	@Column(name = "mvp_prefijo")
	private Integer prefijo = 0;

	@Column(name = "mvp_nrocomprob")
	private Long numeroComprobante = 0L;

	@Column(name = "mvp_netosindescuento")
	private BigDecimal netoSinDescuento = BigDecimal.ZERO;

	@Column(name = "mvp_descuento")
	private BigDecimal descuento = BigDecimal.ZERO;

	@Column(name = "mvp_neto")
	private BigDecimal neto = BigDecimal.ZERO;

	@Column(name = "mvp_importe")
	private BigDecimal importe = BigDecimal.ZERO;

	@Column(name = "mvp_cancelado")
	private BigDecimal cancelado = BigDecimal.ZERO;

	@Column(name = "mvp_fechareg")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fechaContable;

	@Column(name = "mvp_nrocomp")
	private Integer ordenContable;

	@Column(name = "mvp_concepto")
	private String concepto;

	@Column(name = "mvp_anulado")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fechaAnulacion;

	@Column(name = "mvp_concargo")
	private Byte conCargo = 0;

	@Column(name = "mvp_solicfactura")
	private Byte solicitaFactura = 0;

	private Integer geograficaId;

	@OneToOne(optional = true)
	@JoinColumn(name = "mvp_tco_id", insertable = false, updatable = false)
	private Comprobante comprobante;

	@OneToOne(optional = true)
	@JoinColumn(name = "mvp_prv_id", insertable = false, updatable = false)
	private Proveedor proveedor;

	@OneToOne(optional = true)
	@JoinColumn(name = "geograficaId", insertable = false, updatable = false)
	private Geografica geografica;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "fad_mvp_id", insertable = false, updatable = false)
	private List<ProveedorArticulo> proveedorArticulos;

}
