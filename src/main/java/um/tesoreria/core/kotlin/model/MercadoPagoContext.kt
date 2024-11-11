package um.tesoreria.core.kotlin.model

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType

import java.time.OffsetDateTime
import java.math.BigDecimal

@Entity
data class MercadoPagoContext(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var mercadoPagoContextId: Long? = null,
    var chequeraCuotaId: Long? = null,
    var initPoint: String? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaVencimiento: OffsetDateTime? = null,
    var importe: BigDecimal = BigDecimal.ZERO,
    var preference: String? = null,
    var activo: Byte = 0,
    var chequeraPagoId: Long? = null,
    var idMercadoPago: String? = null,
    var status: String? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaPago: OffsetDateTime? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaAcreditacion: OffsetDateTime? = null,

    var importePagado: BigDecimal = BigDecimal.ZERO,
    var payment: String? = null

) : Auditable() {

    class Builder {
        private var mercadoPagoContextId: Long? = null
        private var chequeraCuotaId: Long? = null
        private var initPoint: String? = null
        private var fechaVencimiento: OffsetDateTime? = null
        private var importe: BigDecimal = BigDecimal.ZERO
        private var preference: String? = null
        private var activo: Byte = 0
        private var chequeraPagoId: Long? = null
        private var idMercadoPago: String? = null
        private var fechaPago: OffsetDateTime? = null
        private var importePagado: BigDecimal = BigDecimal.ZERO
        private var payment: String? = null
        private var status: String? = null
        private var fechaAcreditacion: OffsetDateTime? = null

        fun mercadoPagoContextId(mercadoPagoContextId: Long?) = apply { this.mercadoPagoContextId = mercadoPagoContextId }
        fun chequeraCuotaId(chequeraCuotaId: Long?) = apply { this.chequeraCuotaId = chequeraCuotaId }
        fun initPoint(initPoint: String?) = apply { this.initPoint = initPoint }
        fun fechaVencimiento(fechaVencimiento: OffsetDateTime?) = apply { this.fechaVencimiento = fechaVencimiento }
        fun importe(importe: BigDecimal) = apply { this.importe = importe }
        fun preference(preference: String?) = apply { this.preference = preference }
        fun activo(activo: Byte) = apply { this.activo = activo }
        fun chequeraPagoId(chequeraPagoId: Long?) = apply { this.chequeraPagoId = chequeraPagoId }
        fun idMercadoPago(idMercadoPago: String?) = apply { this.idMercadoPago = idMercadoPago }
        fun fechaPago(fechaPago: OffsetDateTime?) = apply { this.fechaPago = fechaPago }
        fun importePagado(importePagado: BigDecimal) = apply { this.importePagado = importePagado }
        fun payment(payment: String?) = apply { this.payment = payment }
        fun status(status: String?) = apply { this.status = status }
        fun fechaAcreditacion(fechaAcreditacion: OffsetDateTime?) = apply { this.fechaAcreditacion = fechaAcreditacion }

        fun build() = MercadoPagoContext(
            mercadoPagoContextId,
            chequeraCuotaId,
            initPoint,
            fechaVencimiento,
            importe,
            preference,
            activo,
            chequeraPagoId,
            idMercadoPago,
            status,
            fechaPago,
            fechaAcreditacion,
            importePagado,
            payment
        )
    }

    companion object {
        fun builder() = Builder()
    }

}
