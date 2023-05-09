package ar.edu.um.tesoreria.rest.extern.model.kotlin

import java.time.OffsetDateTime

data class PlanFacultad(

    var facultadId: Int? = null,
    var planId: Int? = null,
    var uniqueId: Long? = null,
    var nombre: String? = null,
    var fecha: OffsetDateTime? = null,
    var publicar: Byte = 0,
    var semanas: Int? = null,

    ) {
    fun getPlanKey(): String {
        return facultadId.toString() + "." + planId
    }
}
