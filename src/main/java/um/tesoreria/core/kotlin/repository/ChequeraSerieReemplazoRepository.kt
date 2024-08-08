package um.tesoreria.core.kotlin.repository

import org.springframework.data.jpa.repository.JpaRepository
import um.tesoreria.core.kotlin.model.ChequeraSerieReemplazo
import java.util.Optional

interface ChequeraSerieReemplazoRepository : JpaRepository<ChequeraSerieReemplazo, Long> {

    fun findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId: Int, tipoChequeraId: Int, chequeraSerieId: Long): Optional<ChequeraSerieReemplazo?>?

}