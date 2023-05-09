package ar.edu.um.tesoreria.rest.extern.model.kotlin

import java.time.OffsetDateTime

data class PreTurnoFacultad(

    var uniqueId: Long? = null,
    var facultadId: Int? = null,
    var lectivoId: Int? = null,
    var geograficaId: Int? = null,
    var turnoId: Int? = null,
    var nombre: String? = null,
    var inicio: OffsetDateTime? = null,
    var fin: OffsetDateTime? = null,

    ) {
    fun getKey(): String? {
        return facultadId.toString() + "." + lectivoId + "." + geograficaId + "." + turnoId
    }

}
