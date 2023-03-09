package ar.edu.um.tesoreria.rest.model.kotlin

import ar.edu.um.tesoreria.rest.model.Auditable
import ar.edu.um.tesoreria.rest.model.ProveedorArticulo
import ar.edu.um.tesoreria.rest.model.ProveedorMovimiento
import jakarta.persistence.*
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
    var track: Track? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "proveedorMovimientoId", insertable = false, updatable = false)
    var proveedorMovimiento: ProveedorMovimiento? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "proveedorArticuloId", insertable = false, updatable = false)
    var proveedorArticulo: ProveedorArticulo? = null

) : Auditable()
