package um.tesoreria.rest.kotlin.repository.view;

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import um.tesoreria.rest.kotlin.model.view.ChequeraSerieAltaFull
import java.time.OffsetDateTime

@Repository
interface ChequeraSerieAltaFullRepository : JpaRepository<ChequeraSerieAltaFull, Long> {
    fun findAllByLectivoIdAndFacultadIdAndGeograficaIdAndTipoChequeraId(lectivoId: Int, facultadId: Int, geograficaId: Int, tipoChequeraId: Int): List<ChequeraSerieAltaFull>

}