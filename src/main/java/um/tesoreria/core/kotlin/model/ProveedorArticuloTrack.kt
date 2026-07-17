package um.tesoreria.core.kotlin.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.persistence.entity.ProveedorMovimientoEntity
import um.tesoreria.core.hexagonal.track.infrastructure.persistence.entity.TrackEntity
import um.tesoreria.core.model.Auditable
import um.tesoreria.core.util.Jsonifier
import java.math.BigDecimal

@Entity
@Table
data class ProveedorArticuloTrack(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var proveedorArticuloTrackId: Long? = null,

    var proveedorMovimientoId: Long? = null,
    var proveedorArticuloId: Long? = null,
    var trackId: Long? = null,
    var importe: BigDecimal = BigDecimal.ZERO,

    @OneToOne(optional = true)
    @JoinColumn(name = "trackId", insertable = false, updatable = false)
    var track: TrackEntity? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "proveedorMovimientoId", insertable = false, updatable = false)
    var proveedorMovimiento: ProveedorMovimientoEntity? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "proveedorArticuloId", insertable = false, updatable = false)
    var proveedorArticulo: ProveedorArticulo? = null

) : Auditable() {
    fun jsonify(): String {
        return Jsonifier.builder(this).build()
    }

    companion object {
        fun builder() = Builder()
    }

    class Builder {
        private var proveedorArticuloTrackId: Long? = null
        private var proveedorMovimientoId: Long? = null
        private var proveedorArticuloId: Long? = null
        private var trackId: Long? = null
        private var importe: BigDecimal = BigDecimal.ZERO
        private var track: TrackEntity? = null
        private var proveedorMovimiento: ProveedorMovimientoEntity? = null
        private var proveedorArticulo: ProveedorArticulo? = null

        fun proveedorArticuloTrackId(proveedorArticuloTrackId: Long?) = apply { this.proveedorArticuloTrackId = proveedorArticuloTrackId }
        fun proveedorMovimientoId(id: Long?) = apply { this.proveedorMovimientoId = id }
        fun proveedorArticuloId(id: Long?) = apply { this.proveedorArticuloId = id }
        fun trackId(id: Long?) = apply { this.trackId = id }
        fun importe(importe: BigDecimal) = apply { this.importe = importe }
        fun track(track: TrackEntity?) = apply { this.track = track }
        fun proveedorMovimiento(proveedorMovimiento: ProveedorMovimientoEntity?) = apply { this.proveedorMovimiento = proveedorMovimiento }
        fun proveedorArticulo(proveedorArticulo: ProveedorArticulo?) = apply { this.proveedorArticulo = proveedorArticulo }

        fun build() = ProveedorArticuloTrack(
            proveedorArticuloTrackId,
            proveedorMovimientoId,
            proveedorArticuloId,
            trackId,
            importe,
            track,
            proveedorMovimiento,
            proveedorArticulo
        )
    }
}
