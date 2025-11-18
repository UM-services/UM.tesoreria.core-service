package um.tesoreria.core.repository

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import um.tesoreria.core.model.MercadoPagoContext
import java.time.OffsetDateTime
import java.util.Arrays
import java.util.Optional

interface MercadoPagoContextRepository : JpaRepository<MercadoPagoContext, Long> {

    fun findAllByChequeraCuotaIdAndActivo(chequeraCuotaId: Long, activo: Byte): List<MercadoPagoContext?>?

    fun findAllByStatusAndChequeraPagoIdIsNull(status: String): List<MercadoPagoContext>

    fun findByChequeraCuotaIdAndActivo(chequeraCuotaId: Long, activo: Byte): Optional<MercadoPagoContext?>?

    fun findByMercadoPagoContextId(mercadoPagoContextId: Long): Optional<MercadoPagoContext?>?

    fun findAllByActivoOrderByMercadoPagoContextIdDesc(activo: Byte): List<MercadoPagoContext?>?

    fun findAllByActivoAndFechaVencimientoBetween(
        activo: Byte,
        desde: OffsetDateTime,
        hasta: OffsetDateTime
    ): List<MercadoPagoContext?>?

}