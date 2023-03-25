/**
 * 
 */
package ar.edu.um.tesoreria.rest.model.view;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import ar.edu.um.tesoreria.rest.kotlin.model.Comprobante;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

import com.fasterxml.jackson.annotation.JsonFormat;

import ar.edu.um.tesoreria.rest.model.Auditable;
import ar.edu.um.tesoreria.rest.model.Cuenta;
import ar.edu.um.tesoreria.rest.model.Proveedor;
import ar.edu.um.tesoreria.rest.model.ProveedorMovimiento;
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
@Table(name = "vw_cuenta_movimiento_asiento")
@Immutable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CuentaMovimientoAsiento extends Auditable implements Serializable {

	private static final long serialVersionUID = -2637480137165120272L;

	@Id
	private Long cuentaMovimientoId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fechaContable;

	private Integer ordenContable;
	private String asiento;
	private Integer item;
	private BigDecimal numeroCuenta;
	private Byte debita = 0;
	private Integer comprobanteId;
	private String concepto;
	private BigDecimal importe = BigDecimal.ZERO;
	private Integer proveedorId;
	private Integer numeroAnulado = 0;
	private Integer version = 0;
	private Long proveedorMovimientoId;
	private Long proveedorMovimientoIdOrdenPago;
	private Byte apertura = 0;

	@OneToOne(optional = true)
	@JoinColumn(name = "numeroCuenta", insertable = false, updatable = false)
	private Cuenta cuenta;

	@OneToOne(optional = true)
	@JoinColumn(name = "proveedorId", insertable = false, updatable = false)
	private Proveedor proveedor;

	@OneToOne(optional = true)
	@JoinColumn(name = "comprobanteId", insertable = false, updatable = false)
	private Comprobante comprobante;

	@OneToOne(optional = true)
	@JoinColumn(name = "proveedorMovimientoId", insertable = false, updatable = false)
	private ProveedorMovimiento proveedorMovimiento;

	@OneToOne(optional = true)
	@JoinColumn(name = "proveedorMovimientoIdOrdenPago", insertable = false, updatable = false)
	private ProveedorMovimiento ordenPago;

}
