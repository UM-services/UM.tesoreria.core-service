package um.tesoreria.core.kotlin.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import um.tesoreria.core.util.Jsonifier
import java.math.BigDecimal

@Entity
@Table(
    name = "movprov_detallefactura",
    uniqueConstraints = [UniqueConstraint(columnNames = ["fad_mvp_id", "fad_orden"])]
)
data class ProveedorArticulo(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fad_id")
    var proveedorArticuloId: Long? = null,

    @Column(name = "fad_mvp_id")
    var proveedorMovimientoId: Long? = null,

    @Column(name = "fad_orden")
    var orden: Int? = null,

    @Column(name = "fad_art_id")
    var articuloId: Long? = null,

    @Column(name = "fad_cantidad")
    var cantidad: BigDecimal = BigDecimal.ZERO,

    @Column(name = "fad_concepto")
    var concepto: String = "",

    @Column(name = "fad_preciounitario")
    var precioUnitario: BigDecimal = BigDecimal.ZERO,

    @Column(name = "fad_preciofinal")
    var precioFinal: BigDecimal = BigDecimal.ZERO,

    @Column(name = "fad_entregado")
    var entregado: BigDecimal = BigDecimal.ZERO,

    @Column(name = "fad_asignado")
    var asignado: BigDecimal = BigDecimal.ZERO,

    @OneToOne(optional = true)
    @JoinColumn(name = "fad_art_id", insertable = false, updatable = false)
    var articulo: Articulo? = null

) : Auditable() {
    fun jsonify(): String {
        return Jsonifier.builder(this).build()
    }
}
