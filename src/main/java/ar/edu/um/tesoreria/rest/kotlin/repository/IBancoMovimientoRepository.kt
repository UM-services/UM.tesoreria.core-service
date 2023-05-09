package ar.edu.um.tesoreria.rest.kotlin.repository

import ar.edu.um.tesoreria.rest.kotlin.model.BancoMovimiento
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import java.time.OffsetDateTime
import java.util.Optional

interface IBancoMovimientoRepository : JpaRepository<BancoMovimiento, Long> {

    fun findByBancoMovimientoId(bancoMovimientoId: Long?): Optional<BancoMovimiento?>?

    fun findByValorMovimientoId(valorMovimientoId: Long?): Optional<BancoMovimiento?>?

    fun findFirstByBancariaIdAndFechaOrderByOrdenDesc(bancariaId: Long?, fecha: OffsetDateTime?): Optional<BancoMovimiento?>?
    @Modifying
    fun deleteByBancoMovimientoId(bancoMovimientoId: Long?)

}