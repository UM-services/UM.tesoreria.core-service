package um.tesoreria.core.kotlin.model.view

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.Immutable

@Entity
@Table(name = "vw_orden_pago_detalle")
@Immutable
data class OrdenPagoDetalle(

    @Id
    var id: String = "",
    var ordenPagoDetalle: String = ""

)
