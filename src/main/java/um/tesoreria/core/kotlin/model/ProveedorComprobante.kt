package um.tesoreria.core.kotlin.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.math.BigDecimal

@Entity
@Table(
    name = "movprov_detallecomprobantes",
    uniqueConstraints = [UniqueConstraint(columnNames = ["opc_orp_id", "opc_mvp_id"])]
)
data class ProveedorComprobante(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "opc_id")
    var proveedorComprobanteId: Long? = null,

    @Column(name = "opc_orp_id")
    var proveedorMovimientoIdOrdenPago: Long? = null,

    @Column(name = "opc_mvp_id")
    var proveedorMovimientoIdComprobante: Long? = null,

    @Column(name = "opc_importe")
    var importe: BigDecimal = BigDecimal.ZERO,

    var trazaContable: Byte = 0,

    ) : Auditable()
