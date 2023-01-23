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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "movprov_detallefactura", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "fad_mvp_id", "fad_orden" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorArticulo extends Auditable implements Serializable {

	private static final long serialVersionUID = -2199814559234298102L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "fad_id")
	private Long proveedorArticuloId;

	@Column(name = "fad_mvp_id")
	private Long proveedorMovimientoId;

	@Column(name = "fad_orden")
	private Integer orden;

	@Column(name = "fad_art_id")
	private Long articuloId;

	@Column(name = "fad_cantidad")
	private BigDecimal cantidad = BigDecimal.ZERO;

	@Column(name = "fad_concepto")
	private String concepto = "";

	@Column(name = "fad_preciounitario")
	private BigDecimal precioUnitario = BigDecimal.ZERO;

	@Column(name = "fad_preciofinal")
	private BigDecimal precioFinal = BigDecimal.ZERO;

	@Column(name = "fad_entregado")
	private BigDecimal entregado = BigDecimal.ZERO;

	@Column(name = "fad_asignado")
	private BigDecimal asignado = BigDecimal.ZERO;

	@OneToOne(optional = true)
	@JoinColumn(name = "fad_art_id", insertable = false, updatable = false)
	private Articulo articulo;

}
