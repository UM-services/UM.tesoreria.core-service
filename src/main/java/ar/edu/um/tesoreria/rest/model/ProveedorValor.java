/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;

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
@Table(name = "movprov_detallevalores", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "opv_orp_id", "opv_orden" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorValor extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2325757459322165022L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "opv_id")
	private Long proveedorValorId;

	@Column(name = "opv_orp_id")
	private Long proveedorMovimientoId;

	@Column(name = "opv_orden")
	private Integer orden;

	@Column(name = "opv_val_id")
	private Long valorMovimientoId;

}
