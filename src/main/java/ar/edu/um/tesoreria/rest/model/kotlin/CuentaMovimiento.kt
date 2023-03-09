package ar.edu.um.tesoreria.rest.model

import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Column
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.OffsetDateTime
import java.math.BigDecimal
import jakarta.persistence.OneToOne
import jakarta.persistence.JoinColumn
import ar.edu.um.tesoreria.rest.model.kotlin.Track

@Entity
@Table(
	name = "movcon",
	uniqueConstraints = [
		UniqueConstraint(columnNames = ["mco_fecha", "mco_nrocomp", "mco_item"])
	]
)
data class CuentaMovimiento(

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	var cuentaMovimientoId: Long? = null,

	@Column(name = "mco_fecha")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	var fechaContable: OffsetDateTime? = null,

	@Column(name = "mco_nrocomp")
	var ordenContable: Int = 0,

	@Column(name = "mco_item")
	var item: Int = 0,

	@Column(name = "mco_pla_cuenta")
	var numeroCuenta: BigDecimal? = null,

	@Column(name = "mco_debita")
	var debita: Byte = 0,

	@Column(name = "mco_tco_id")
	var comprobanteId: Int? = null,

	@Column(name = "mco_concepto")
	var concepto: String = "",

	@Column(name = "mco_importe")
	var importe: BigDecimal = BigDecimal.ZERO,

	@Column(name = "mco_prv_id")
	var proveedorId: Int? = null,

	@Column(name = "mco_nroanulado")
	var numeroAnulado: Int = 0,

	var version: Int = 0,

	@Column(name = "proveedormovimiento_id")
	var proveedorMovimientoId: Long? = null,

	@Column(name = "proveedormovimiento_id_orden_pago")
	var proveedorMovimientoIdOrdenPago: Long? = null,

	var apertura: Byte = 0,

	var trackId: Long? = null,

	@OneToOne(optional = true)
	@JoinColumn(name = "mco_pla_cuenta", insertable = false, updatable = false)
	var cuenta: Cuenta? = null,

	@OneToOne(optional = true)
	@JoinColumn(name = "mco_prv_id", insertable = false, updatable = false)
	var proveedor: Proveedor? = null,

	@OneToOne(optional = true)
	@JoinColumn(name = "mco_tco_id", insertable = false, updatable = false)
	var comprobante: Comprobante? = null,

	@OneToOne(optional = true)
	@JoinColumn(name = "proveedormovimiento_id", insertable = false, updatable = false)
	var proveedorMovimiento: ProveedorMovimiento? = null,

	@OneToOne(optional = true)
	@JoinColumn(name = "proveedormovimiento_id_orden_pago", insertable = false, updatable = false)
	var ordenPago: ProveedorMovimiento? = null,

	@OneToOne(optional = true)
	@JoinColumn(name = "trackId", insertable = false, updatable = false)
	var track: Track? = null

) : Auditable()