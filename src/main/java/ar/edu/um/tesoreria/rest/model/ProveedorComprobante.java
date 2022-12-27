/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

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
@Table(name = "movprov_detallecomprobantes", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "opc_orp_id", "opc_mvp_id" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorComprobante extends Auditable implements Serializable {

	private static final long serialVersionUID = 9157338258714513007L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "opc_id")
	private Long proveedorComprobanteId;

	@Column(name = "opc_orp_id")
	private Long proveedorMovimientoIdOrdenPago;

	@Column(name = "opc_mvp_id")
	private Long proveedorMovimientoIdComprobante;

	@Column(name = "opc_importe")
	private BigDecimal importe = BigDecimal.ZERO;

	private Byte trazaContable = 0;
	
}
