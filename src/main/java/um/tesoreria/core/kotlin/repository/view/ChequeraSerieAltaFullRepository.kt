package um.tesoreria.core.kotlin.repository.view;

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import um.tesoreria.core.kotlin.model.view.ChequeraSerieAltaFull

@Repository
interface ChequeraSerieAltaFullRepository : JpaRepository<ChequeraSerieAltaFull, Long> {
    fun findAllByLectivoIdAndFacultadIdAndGeograficaIdAndTipoChequeraId(lectivoId: Int, facultadId: Int, geograficaId: Int, tipoChequeraId: Int): List<ChequeraSerieAltaFull>

}