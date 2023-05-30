package um.tesoreria.rest.kotlin.model

import um.tesoreria.rest.model.Auditable
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

    var trazaContable: Byte = 0,

    ) : Auditable()
