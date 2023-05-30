package um.tesoreria.rest.kotlin.repository

import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import um.tesoreria.rest.kotlin.model.ValorMovimiento
import java.time.OffsetDateTime
import java.util.Optional

interface IValorMovimientoRepository : JpaRepository<ValorMovimiento, Long> {

    fun findAllByValorMovimientoIdIn(valorMovimientoIds: List<Long?>?): List<ValorMovimiento?>?

    fun findAllByValorMovimientoIdInAndEstadoNotAndEstadoNot(valorMovimientoIds: List<Long?>?, Anulado: String, Reemplazado: String, sort: Sort): List<ValorMovimiento?>?

    fun findFirstByValorIdAndNumero(valorId: Int?, numero: Long?): Optional<ValorMovimiento?>?

    fun findFirstByValorIdAndNumeroAndBancariaIdOrigen(valorId: Int?, numero: Long?, bancariaId: Long?): Optional<ValorMovimiento?>?

    fun findByValorMovimientoId(valorMovimientoId: Long?): Optional<ValorMovimiento?>?

    fun findFirstByFechaContableAndOrdenContable(fechaContable: OffsetDateTime?, ordenContable: Int?): Optional<ValorMovimiento?>?

    @Modifying
    fun deleteByValorMovimientoId(valorMovimientoId: Long?)

}