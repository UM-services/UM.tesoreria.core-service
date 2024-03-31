package um.tesoreria.core.extern.model.kotlin

import java.math.BigDecimal
import java.time.OffsetDateTime

data class PreInscripcionFacultad(

    var preinscripcionId: Long? = null,
    var facultadId: Int? = null,
    var lectivoId: Int? = null,
    var geograficaId: Int? = null,
    var turnoId: Int? = null,
    var personaId: BigDecimal? = null,
    var documentoId: Int? = null,
    var fecha: OffsetDateTime? = null,
    var chequera: String? = null,
    var observaciones: String? = null,
    var barras: String? = null,

    ) {
    fun getPersonaKey(): String? {
        return personaId.toString() + "." + documentoId
    }
}
