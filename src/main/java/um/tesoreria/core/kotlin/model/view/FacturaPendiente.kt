package um.tesoreria.core.kotlin.model.view

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Immutable
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(name = "vw_factura_pendiente")
@Immutable
data class FacturaPendiente(

    @Id
    val proveedorMovimientoId: Long?,
    val razonSocial: String,
    val cuit: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    val fechaComprobante: OffsetDateTime?,
    val comprobante: String,
    val prefijo: Int,
    val numeroComprobante: Long,
    val importeFactura: BigDecimal,
    val ordenPagoId: Long?,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    val fechaOrdenPago: OffsetDateTime?,
    val prefijoOrdenPago: Int?,
    val numeroOrdenPago: Long?,
    val importeOrdenPago: BigDecimal?,
    val importePagado: BigDecimal?
)