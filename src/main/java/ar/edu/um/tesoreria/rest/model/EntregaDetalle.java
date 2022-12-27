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
import jakarta.persistence.JoinColumns;
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
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = { "ned_noe_id", "ned_art_id", "ned_fad_mvp_id", "ned_fad_orden" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class EntregaDetalle extends Auditable implements Serializable {

	private static final long serialVersionUID = 47210684696976092L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ned_id")
	private Long entregaDetalleId;

	@Column(name = "ned_noe_id")
	private Long entregaId;

	@Column(name = "ned_art_id")
	private Long articuloId;

	@Column(name = "ned_fad_mvp_id")
	private Long proveedorMovimientoId;

	@Column(name = "ned_fad_orden")
	private Integer orden = 0;

	@Column(name = "ned_concepto")
	private String concepto = "";

	@Column(name = "ned_cantidad")
	private BigDecimal cantidad = BigDecimal.ZERO;

	@OneToOne(optional = true)
	@JoinColumn(name = "ned_noe_id", insertable = false, updatable = false)
	private Entrega entrega;

	@OneToOne(optional = true)
	@JoinColumn(name = "ned_art_id", insertable = false, updatable = false)
	private Articulo articulo;

	@OneToOne(optional = true)
	@JoinColumn(name = "ned_fad_mvp_id", insertable = false, updatable = false)
	private ProveedorMovimiento proveedorMovimiento;

	@OneToOne(optional = true)
	@JoinColumns({
			@JoinColumn(name = "ned_fad_mvp_id", referencedColumnName = "fad_mvp_id", insertable = false, updatable = false),
			@JoinColumn(name = "ned_fad_orden", referencedColumnName = "fad_orden", insertable = false, updatable = false) })
	private ProveedorArticulo proveedorArticulo;

}
