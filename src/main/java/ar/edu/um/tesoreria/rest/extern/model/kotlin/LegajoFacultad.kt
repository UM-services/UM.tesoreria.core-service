package ar.edu.um.tesoreria.rest.extern.model.kotlin

import java.math.BigDecimal
import java.time.OffsetDateTime

data class LegajoFacultad(

    var personaId: BigDecimal? = null,
    var documentoId: Int? = null,
    var facultadId: Int? = null,
    var legajoId: Long? = null,
    var geograficaId: Int? = null,
    var numeroLegajo: Long? = null,
    var lectivoId: Int? = null,
    var fecha: OffsetDateTime? = null,
    var planId: Int? = null,
    var carreraId: Int? = null,
    var contrasenha: String = "",
    var intercambio: Byte = 0,

)
