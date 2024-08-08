package um.tesoreria.core.kotlin.repository

import org.springframework.data.jpa.repository.JpaRepository
import um.tesoreria.core.kotlin.model.ChequeraCuotaReemplazo

interface ChequeraCuotaReemplazoRepository : JpaRepository<ChequeraCuotaReemplazo, Long> {

    fun findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(
        facultadId: Int,
        tipoChequeraId: Int,
        chequeraSerieId: Long,
        alternativaId: Int
    ): List<ChequeraCuotaReemplazo?>?

}