package um.tesoreria.core.kotlin.model.view

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import org.hibernate.annotations.Immutable
import um.tesoreria.core.kotlin.model.*
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(
    name = "vw_chequera_serie_alta_full",
    uniqueConstraints = [UniqueConstraint(columnNames = ["facultadId", "tipoChequeraId", "chequeraSerieId"])]
)
@Immutable
data class ChequeraSerieAltaFull(

    @Id
    var chequeraId: Long? = null,
    var facultadId: Int? = null,
    var tipoChequeraId: Int? = null,
    var chequeraSerieId: Long? = null,
    var personaId: BigDecimal? = null,
    var documentoId: Int? = null,
    var lectivoId: Int? = null,
    var arancelTipoId: Int? = null,
    var cursoId: Int? = null,
    var asentado: Int? = null,
    var geograficaId: Int? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fecha: OffsetDateTime? = null,
    var cuotasPagadas: Int? = null,
    var observaciones: String? = null,
    var alternativaId: Int? = null,
    var algoPagado: Byte? = null,
    var tipoImpresionId: Int? = null,
    var flagPayperTic: Byte? = null,
    var usuarioId: String? = null,
    var enviado: Byte? = null,

    @OneToOne
    @JoinColumn(name = "facultadId", insertable = false, updatable = false)
    var facultad: Facultad? = null,

    @OneToOne
    @JoinColumn(name = "tipoChequeraId", insertable = false, updatable = false)
    var tipoChequera: TipoChequera? = null,

    @OneToOne
    @JoinColumns(
        JoinColumn(name = "personaId", referencedColumnName = "per_id", insertable = false, updatable = false),
        JoinColumn(name = "documentoId", referencedColumnName = "per_doc_id", insertable = false, updatable = false)
    )
    var persona: Persona? = null,

    @OneToOne
    @JoinColumns(
        JoinColumn(name = "personaId", referencedColumnName = "dom_per_id", insertable = false, updatable = false),
        JoinColumn(name = "documentoId", referencedColumnName = "dom_doc_id", insertable = false, updatable = false)
    )
    var domicilio: Domicilio? = null,

    @OneToOne
    @JoinColumn(name = "lectivoId", insertable = false, updatable = false)
    var lectivo: Lectivo? = null,

    @OneToOne
    @JoinColumn(name = "arancelTipoId", insertable = false, updatable = false)
    var arancelTipo: ArancelTipo? = null,

    @OneToOne
    @JoinColumn(name = "geograficaId", insertable = false, updatable = false)
    var geografica: Geografica? = null

) : Auditable()
