package um.tesoreria.core.kotlin.model

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(
    name = "movprov_detallecomprobantes",
    uniqueConstraints = [UniqueConstraint(columnNames = ["opc_orp_id", "opc_mvp_id"])]
)
data class ProveedorPago(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "opc_id")
    var proveedorPagoId: Long? = null,

    @Column(name = "opc_orp_id")
    var proveedorMovimientoIdPago: Long? = null,

    @Column(name = "opc_mvp_id")
    var proveedorMovimientoIdFactura: Long? = null,

    @Column(name = "opc_importe")
    var importeAplicado: BigDecimal = BigDecimal.ZERO,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaPago: OffsetDateTime? = null,

    var trazaContable: Byte = 0,

    @OneToOne(optional = true)
    @JoinColumn(name = "opc_orp_id", insertable = false, updatable = false)
    var ordenPago: ProveedorMovimiento? = null,

) : Auditable()
