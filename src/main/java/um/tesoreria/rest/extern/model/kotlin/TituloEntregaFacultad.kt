package um.tesoreria.rest.extern.model.kotlin

import java.math.BigDecimal
import java.time.OffsetDateTime

data class TituloEntregaFacultad(

    var personaId: BigDecimal? = null,
    var documentoId: Int? = null,
    var facultadId: Int? = null,
    var planId: Int? = null,
    var carreraId: Int? = null,
    var tituloentregaId: Long? = null,
    var inicio: OffsetDateTime? = null,
    var entrega: OffsetDateTime? = null,
    var folio: Long? = null,
    var libro: Int? = null,
    var ministerio: String? = null,
    var formula: String? = null,
    var titulogrado: String? = null,
    var titulotrabajo: String? = null,
    var personaIdasesor: BigDecimal? = null,
    var documentoIdasesor: Int? = null,
    var fechaultima: OffsetDateTime? = null,
    var materiaIdultima: String? = null,
    var libroultima: String? = null,
    var folioultima: Long? = null,
    var geograficaId: Int? = null,

    )
