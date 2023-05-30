package um.tesoreria.rest.kotlin.model

import um.tesoreria.rest.model.Auditable
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["facultadId", "tipoChequeraId", "chequeraSerieId"])])
data class ChequeraEliminada(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var chequeraEliminadaId: Long? = null,

    var facultadId: Int? = null,
    var tipoChequeraId: Int? = null,
    var chequeraSerieId: Long? = null,
    var personaId: BigDecimal? = null,
    var documentoId: Int? = null,
    var lectivoId: Int? = null,
    var arancelTipoId: Int? = null,
    var cursoId: Int? = null,
    var asentado: Byte = 0,
    var geograficaId: Int? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fecha: OffsetDateTime? = null,

    var cuotasPagadas: Int = 0,
    var observaciones: String = "",
    var alternativaId: Int? = null,
    var algoPagado: Byte = 0,
    var tipoImpresionId: Int? = null,
    var usuarioId: String? = null,

    ) : Auditable()
