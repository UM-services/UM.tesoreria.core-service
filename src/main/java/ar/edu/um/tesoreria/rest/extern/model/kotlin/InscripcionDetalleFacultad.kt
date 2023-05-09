package ar.edu.um.tesoreria.rest.extern.model.kotlin

import java.math.BigDecimal

data class InscripcionDetalleFacultad(

    var personaId: BigDecimal? = null,
    var documentoId: Int? = null,
    var lectivoId: Int? = null,
    var facultadId: Int? = null,
    var planId: Int? = null,
    var materiaId: String? = null,
    var inscripciondetalleId: Long? = null,
    var cursoId: Int = 0,
    var periodoId: Int = 0,
    var divisionId: Int? = null,
    var recursa: Byte = 0,
    var imprimir: Byte = 0,
    var moroso: Byte = 0,
    var libre: Byte = 0,
    var condicional: Byte = 0,

    )
