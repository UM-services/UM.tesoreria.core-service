package um.tesoreria.core.kotlin.model

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import um.tesoreria.core.hexagonal.persona.infrastructure.persistence.entity.PersonaEntity
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["CSR_Fac_ID", "CSR_TCh_ID", "CSR_ChS_ID"])])
data class ChequeraSerieReemplazo(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clave")
    var chequeraReemplazoId: Long? = null,

    @Column(name = "CSR_Fac_ID")
    var facultadId: Int? = null,

    @Column(name = "CSR_TCh_ID")
    var tipoChequeraId: Int? = null,

    @Column(name = "CSR_ChS_ID")
    var chequeraSerieId: Long? = null,

    @Column(name = "CSR_Per_ID")
    var personaId: BigDecimal? = null,

    @Column(name = "CSR_Doc_ID")
    var documentoId: Int? = null,

    @Column(name = "CSR_Lec_ID")
    var lectivoId: Int? = null,

    @Column(name = "CSR_ArT_ID")
    var arancelTipoId: Long? = null,

    @Column(name = "CSR_Cur_ID")
    var cursoId: Int? = null,

    @Column(name = "CSR_Asentado")
    var asentado: Int = 0,

    @Column(name = "CSR_Geo_ID")
    var geograficaId: Int? = 1,

    @Column(name = "CSR_Fecha")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fecha: OffsetDateTime,

    @Column(name = "CSR_CuotasP")
    var cuotasPagadas: Int = 0,

    @Column(name = "CSR_Observ")
    var observaciones: String = "",

    @Column(name = "CSR_Alt_ID")
    var alternativaId: Int? = 1,

    @Column(name = "csr_algopagado")
    var algoPagado: Byte = 0,

    @Column(name = "CSR_TIm_ID")
    var tipoImpresionId: Int? = null,

    var usuarioId: String? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "CSR_Fac_ID", insertable = false, updatable = false)
    var facultad: Facultad? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "CSR_TCh_ID", insertable = false, updatable = false)
    var tipoChequera: TipoChequera? = null,

    @OneToOne(optional = true)
    @JoinColumns(
        JoinColumn(name = "CSR_Per_ID", referencedColumnName = "per_id", insertable = false, updatable = false),
        JoinColumn(name = "CSR_Doc_ID", referencedColumnName = "per_doc_id", insertable = false, updatable = false)
    )
    var personaEntity: PersonaEntity? = null,

    @OneToOne(optional = true)
    @JoinColumns(
        JoinColumn(
            name = "CSR_Per_ID",
            referencedColumnName = "dom_per_id",
            insertable = false,
            updatable = false
        ),
        JoinColumn(
            name = "CSR_Doc_ID",
            referencedColumnName = "dom_doc_id",
            insertable = false,
            updatable = false
        )
    )
    var domicilio: Domicilio? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "CSR_Lec_ID", insertable = false, updatable = false)
    var lectivo: Lectivo? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "CSR_ArT_ID", insertable = false, updatable = false)
    var arancelTipo: ArancelTipo? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "CSR_Geo_ID", insertable = false, updatable = false)
    var geografica: Geografica? = null

) : Auditable()
