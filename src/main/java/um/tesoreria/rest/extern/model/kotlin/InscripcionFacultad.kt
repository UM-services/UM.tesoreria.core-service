package um.tesoreria.rest.extern.model.kotlin

import java.math.BigDecimal
import java.time.OffsetDateTime

data class InscripcionFacultad(

    var facultadId: Int? = null,
    var personaId: BigDecimal? = null,
    var documentoId: Int? = null,
    var lectivoId: Int? = null,
    var inscripcionId: Long? = null,
    var fecha: OffsetDateTime? = null,
    var chequera: String = "",
    var matricula: String = "",
    var factura: Long = 0L,
    var curso: Int = 0,
    var planId: Int? = null,
    var carreraId: Int? = null,
    var geograficaId: Int? = null,
    var asentado: Byte = 0,
    var provisoria: Byte = 0,
    var cohorte: Int = 0,
    var remota: Byte = 0,
    var imprimir: Byte = 0,
    var edad: Int = 0,
    var observaciones: String = "",
    var offsetpago: Int = 0,
    var libre: Int = 0,
    var divisionId: Int? = null,
    var debematricula: Byte = 0,

) {

    fun getPersonaKey(): String {
        return personaId.toString() + "." + documentoId
    }

    fun getCarreraKey(): String {
        return facultadId.toString() + "." + planId + "." + carreraId
    }

    fun getPlanKey(): String {
        return facultadId.toString() + "." + planId
    }

    fun getLegajoKey(): String {
        return facultadId.toString() + "." + personaId + "." + documentoId
    }

}
