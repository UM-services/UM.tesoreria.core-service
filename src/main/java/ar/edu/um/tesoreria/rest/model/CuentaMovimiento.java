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
import jakarta.persistence.JoinColumn;
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
@Table(name = "movcon", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "mco_fecha", "mco_nrocomp", "mco_item" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class CuentaMovimiento extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2274055553288277343L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long cuentaMovimientoId;

	@Column(name = "mco_fecha")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fechaContable;

	@Column(name = "mco_nrocomp")
	private Integer ordenContable;

	@Column(name = "mco_item")
	private Integer item;

	@Column(name = "mco_pla_cuenta")
	private BigDecimal numeroCuenta;

	@Column(name = "mco_debita")
	private Byte debita = 0;

	@Column(name = "mco_tco_id")
	private Integer comprobanteId;

	@Column(name = "mco_concepto")
	private String concepto;

	@Column(name = "mco_importe")
	private BigDecimal importe = BigDecimal.ZERO;

	@Column(name = "mco_prv_id")
	private Integer proveedorId;

	@Column(name = "mco_nroanulado")
	private Integer numeroAnulado = 0;

	private Integer version = 0;

	@Column(name = "proveedormovimiento_id")
	private Long proveedorMovimientoId;

	@Column(name = "proveedormovimiento_id_orden_pago")
	private Long proveedorMovimientoIdOrdenPago;

	private Byte apertura = 0;

	@OneToOne(optional = true)
	@JoinColumn(name = "mco_pla_cuenta", insertable = false, updatable = false)
	private Cuenta cuenta;

	@OneToOne(optional = true)
	@JoinColumn(name = "mco_prv_id", insertable = false, updatable = false)
	private Proveedor proveedor;

	@OneToOne(optional = true)
	@JoinColumn(name = "mco_tco_id", insertable = false, updatable = false)
	private Comprobante comprobante;

	@OneToOne(optional = true)
	@JoinColumn(name = "proveedormovimiento_id", insertable = false, updatable = false)
	private ProveedorMovimiento proveedorMovimiento;

	@OneToOne(optional = true)
	@JoinColumn(name = "proveedormovimiento_id_orden_pago", insertable = false, updatable = false)
	private ProveedorMovimiento ordenPago;

}
