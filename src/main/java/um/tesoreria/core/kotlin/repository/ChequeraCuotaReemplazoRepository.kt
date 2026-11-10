package um.tesoreria.core.kotlin.repository

import org.springframework.data.jpa.repository.JpaRepository
import um.tesoreria.core.kotlin.model.ChequeraCuotaReemplazo
import java.util.Optional

interface ChequeraCuotaReemplazoRepository : JpaRepository<ChequeraCuotaReemplazo, Long> {

    fun findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(
        facultadId: Int,
        tipoChequeraId: Int,
        chequeraSerieId: Long,
        alternativaId: Int
    ): List<ChequeraCuotaReemplazo?>?

    fun findByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaId(
        facultadId: Int,
        tipoChequeraId: Int,
        chequeraSerieId: Long,
        productoId: Int,
        alternativaId: Int,
        cuotaId: Int
    ): Optional<ChequeraCuotaReemplazo>

    fun findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndCuotaIdAndMesAndAnho(
        facultadId: Int,
        tipoChequeraId: Int,
        chequeraSerieId: Long,
        productoId: Int,
        cuotaId: Int,
        mes: Int,
        anho: Int
    ): List<ChequeraCuotaReemplazo?>

}
