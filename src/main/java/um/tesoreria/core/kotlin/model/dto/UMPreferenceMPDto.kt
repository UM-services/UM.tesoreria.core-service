package um.tesoreria.core.kotlin.model.dto

import um.tesoreria.core.kotlin.model.ChequeraCuota
import um.tesoreria.core.kotlin.model.MercadoPagoContext

data class UMPreferenceMPDto(
    var mercadoPagoContext: MercadoPagoContext? = null,
    var chequeraCuota: ChequeraCuota? = null
) {
    // Builder class
    class Builder {
        private var mercadoPagoContext: MercadoPagoContext? = null
        private var chequeraCuota: ChequeraCuota? = null

        fun mercadoPagoContext(mercadoPagoContext: MercadoPagoContext?) = apply {
            this.mercadoPagoContext = mercadoPagoContext
        }

        fun chequeraCuota(chequeraCuota: ChequeraCuota?) = apply {
            this.chequeraCuota = chequeraCuota
        }

        fun build() = UMPreferenceMPDto(
            mercadoPagoContext = mercadoPagoContext,
            chequeraCuota = chequeraCuota
        )
    }

    companion object {
        fun builder() = Builder()
    }
}
