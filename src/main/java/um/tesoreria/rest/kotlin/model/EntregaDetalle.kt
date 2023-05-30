package um.tesoreria.rest.kotlin.model

import um.tesoreria.rest.model.Auditable
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Column
import jakarta.persistence.OneToOne
import jakarta.persistence.JoinColumn
import java.math.BigDecimal

@Entity
@Table(
	uniqueConstraints = [
		UniqueConstraint(columnNames = ["ned_noe_id", "ned_art_id", "ned_fad_mvp_id", "ned_fad_orden"])
	]
)
data class EntregaDetalle(

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ned_id")
	var entregaDetalleId: Long? = null,

	@Column(name = "ned_noe_id")
	var entregaId: Long? = null,

	@Column(name = "ned_art_id")
	var articuloId: Long? = null,

	@Column(name = "ned_fad_mvp_id")
	var proveedorMovimientoId: Long? = null,

	@Column(name = "ned_fad_orden")
	var orden: Int = 0,

	@Column(name = "ned_concepto")
	var concepto: String = "",

	@Column(name = "ned_cantidad")
	var cantidad: BigDecimal = BigDecimal.ZERO,

	var trackId: Long? = null,

	@OneToOne(optional = true)
	@JoinColumn(name = "ned_noe_id", insertable = false, updatable = false)
	var entrega: Entrega? = null,

	@OneToOne(optional = true)
	@JoinColumn(name = "ned_art_id", insertable = false, updatable = false)
	var articulo: Articulo? = null,

	@OneToOne(optional = true)
	@JoinColumn(name = "ned_fad_mvp_id", insertable = false, updatable = false)
	var proveedorMovimiento: ProveedorMovimiento? = null,

	@OneToOne(optional = true)
	@JoinColumn(name = "ned_fad_mvp_id", referencedColumnName = "fad_mvp_id", insertable = false, updatable = false)
	@JoinColumn(name = "ned_fad_orden", referencedColumnName = "fad_orden", insertable = false, updatable = false)
	var proveedorArticulo: ProveedorArticulo? = null,

	@OneToOne(optional = true)
	@JoinColumn(name = "trackId", insertable = false, updatable = false)
	var track: Track? = null

) : Auditable()
