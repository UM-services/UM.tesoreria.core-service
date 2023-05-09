package ar.edu.um.tesoreria.rest.extern.model.kotlin

import java.math.BigDecimal
import java.time.OffsetDateTime

data class BajaFacultad(

    var facultadId: Int? = null,
    var personaId: BigDecimal? = null,
    var documentoId: Int? = null,
    var lectivoId: Int? = null,
    var bajaId: Long? = null,
    var definitiva: Byte = 0,
    var certificadoanaliticoparcial: Byte = 0,
    var libredeuda: Byte = 0,
    var previa: Byte = 0,
    var temporaria: Byte = 0,
    var motivo: String? = null,
    var fecha: OffsetDateTime? = null,

    )
