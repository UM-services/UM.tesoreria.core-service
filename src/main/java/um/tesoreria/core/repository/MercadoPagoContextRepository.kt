package um.tesoreria.core.repository

import org.springframework.data.jpa.repository.JpaRepository
import um.tesoreria.core.model.MercadoPagoContext
import java.util.Arrays
import java.util.Optional

interface MercadoPagoContextRepository : JpaRepository<MercadoPagoContext, Long> {

    fun findAllByChequeraCuotaIdAndActivo(chequeraCuotaId: Long, activo: Byte): List<MercadoPagoContext?>?

    fun findAllByStatusAndChequeraPagoIdIsNull(status: String): List<MercadoPagoContext>

    fun findByChequeraCuotaIdAndActivo(chequeraCuotaId: Long, activo: Byte): Optional<MercadoPagoContext?>?

    fun findByMercadoPagoContextId(mercadoPagoContextId: Long): Optional<MercadoPagoContext?>?

    fun findAllByActivo(activo: Byte): List<MercadoPagoContext?>?

}