package um.tesoreria.core.kotlin.model

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
data class EntregaDetalle @JvmOverloads constructor(

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

    var proveedorArticuloId: Long? = null,

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

) : Auditable() {

    companion object {
        fun builder() = Builder()
    }

    class Builder {
        private var entregaDetalleId: Long? = null
        private var entregaId: Long? = null
        private var articuloId: Long? = null
        private var proveedorMovimientoId: Long? = null
        private var orden: Int = 0
        private var concepto: String = ""
        private var cantidad: BigDecimal = BigDecimal.ZERO
        private var trackId: Long? = null
        private var proveedorArticuloId: Long? = null
        private var entrega: Entrega? = null
        private var articulo: Articulo? = null
        private var proveedorMovimiento: ProveedorMovimiento? = null
        private var proveedorArticulo: ProveedorArticulo? = null
        private var track: Track? = null

        fun entregaDetalleId(entregaDetalleId: Long?) = apply { this.entregaDetalleId = entregaDetalleId }
        fun entregaId(id: Long?) = apply { this.entregaId = id }
        fun articuloId(id: Long?) = apply { this.articuloId = id }
        fun proveedorMovimientoId(id: Long?) = apply { this.proveedorMovimientoId = id }
        fun orden(orden: Int) = apply { this.orden = orden }
        fun concepto(concepto: String) = apply { this.concepto = concepto }
        fun cantidad(cantidad: BigDecimal) = apply { this.cantidad = cantidad }
        fun trackId(id: Long?) = apply { this.trackId = id }
        fun proveedorArticuloId(id: Long?) = apply { this.proveedorArticuloId = id }
        fun entrega(entrega: Entrega?) = apply { this.entrega = entrega }
        fun articulo(articulo: Articulo?) = apply { this.articulo = articulo }
        fun proveedorMovimiento(proveedorMovimiento: ProveedorMovimiento?) = apply { this.proveedorMovimiento = proveedorMovimiento }
        fun proveedorArticulo(proveedorArticulo: ProveedorArticulo?) = apply { this.proveedorArticulo = proveedorArticulo }
        fun track(track: Track?) = apply { this.track = track }

        fun build() = EntregaDetalle(
            entregaDetalleId,
            entregaId,
            articuloId,
            proveedorMovimientoId,
            orden,
            concepto,
            cantidad,
            trackId,
            proveedorArticuloId,
            entrega,
            articulo,
            proveedorMovimiento,
            proveedorArticulo,
            track
        )
    }
}
