package um.tesoreria.core.kotlin.repository.view;

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import um.tesoreria.core.kotlin.model.view.TipoChequeraFacultadLectivoGeograficaCantidad

@Repository
interface TipoChequeraFacultadLectivoGeograficaCantidadRepository :
    JpaRepository<TipoChequeraFacultadLectivoGeograficaCantidad, String> {

    fun findAllByFacultadIdAndLectivoIdAndGeograficaId(facultadId: Int, lectivoId: Int, geograficaId: Int): List<TipoChequeraFacultadLectivoGeograficaCantidad>

}