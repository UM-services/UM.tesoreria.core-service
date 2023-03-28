package ar.edu.um.tesoreria.rest.kotlin.model

import ar.edu.um.tesoreria.rest.model.Auditable
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table
data class FacturacionElectronica(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var facturacionElectronicaId: Long? = null,

    var chequeraPagoId: Long? = null,
    var comprobanteId: Int? = null,
    var numeroComprobante: Long = 0,
    var personaId: BigDecimal? = null,
    var apellido: String? = null,
    var nombre: String? = null,
    var cuit: String? = null,
    var importe: BigDecimal = BigDecimal.ZERO,
    var cae: String? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaRecibo: OffsetDateTime? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaVencimientoCae: OffsetDateTime? = null,

    ) : Auditable()
