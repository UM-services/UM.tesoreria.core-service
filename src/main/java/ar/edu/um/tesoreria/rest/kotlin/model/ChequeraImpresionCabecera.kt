package ar.edu.um.tesoreria.rest.kotlin.model

import ar.edu.um.tesoreria.rest.model.Auditable
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(name = "chequera_c_impresion")
data class ChequeraImpresionCabecera(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var chequeraImpresionCabeceraId: Long? = null,

    var facultadId: Int? = null,
    var tipochequeraId: Int? = null,
    var chequeraserieId: Long? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fecha: OffsetDateTime? = null,

    var personaId: BigDecimal? = null,
    var documentoId: Int? = null,
    var lectivoId: Int? = null,
    var geograficaId: Int? = null,
    var aranceltipoId: Int? = null,
    var alternativaId: Int? = null,
    var usuarioId: String? = null,

    ) : Auditable()
