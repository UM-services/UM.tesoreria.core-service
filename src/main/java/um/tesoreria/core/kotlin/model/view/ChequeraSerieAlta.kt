package um.tesoreria.core.kotlin.model.view

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import org.hibernate.annotations.Immutable
import um.tesoreria.core.kotlin.model.*
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(
    name = "vw_chequera_alta",
    uniqueConstraints = [UniqueConstraint(columnNames = ["chs_fac_id", "chs_tch_id", "chs_id"])]
)
@Immutable
data class ChequeraSerieAlta(

    @Id
    @Column(name = "clave")
    var chequeraId: Long? = null,

    @Column(name = "chs_fac_id")
    var facultadId: Int? = null,

    @Column(name = "chs_tch_id")
    var tipoChequeraId: Int? = null,

    @Column(name = "chs_id")
    var chequeraSerieId: Long? = null,

    @Column(name = "chs_per_id")
    var personaId: BigDecimal? = null,

    @Column(name = "chs_doc_id")
    var documentoId: Int? = null,

    @Column(name = "chs_lec_id")
    var lectivoId: Int? = null,

    @Column(name = "chs_art_id")
    var arancelTipoId: Int? = null,

    @Column(name = "chs_cur_id")
    var cursoId: Int? = null,

    @Column(name = "chs_asentado")
    var asentado: Int? = null,

    @Column(name = "chs_geo_id")
    var geograficaId: Int? = null,

    @Column(name = "chs_fecha")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fecha: OffsetDateTime? = null,

    @Column(name = "chs_cuotasp")
    var cuotasPagadas: Int? = null,

    @Column(name = "chs_observ")
    var observaciones: String? = null,

    @Column(name = "chs_alt_id")
    var alternativaId: Int? = null,

    @Column(name = "chs_algopagado")
    var algoPagado: Byte? = null,

    @Column(name = "chs_tim_id")
    var tipoImpresionId: Int? = null,

    @Column(name = "flag_paypertic")
    var flagPayperTic: Byte? = null,

    var usuarioId: String? = null,
    var enviado: Byte? = null,

    @OneToOne
    @JoinColumn(name = "chs_fac_id", insertable = false, updatable = false)
    var facultad: Facultad? = null,

    @OneToOne
    @JoinColumn(name = "chs_tch_id", insertable = false, updatable = false)
    var tipoChequera: TipoChequera? = null,

    @OneToOne
    @JoinColumns(
        JoinColumn(name = "chs_per_id", referencedColumnName = "per_id", insertable = false, updatable = false),
        JoinColumn(name = "chs_doc_id", referencedColumnName = "per_doc_id", insertable = false, updatable = false)
    )
    var persona: Persona? = null,

    @OneToOne
    @JoinColumns(
        JoinColumn(
            name = "chs_per_id",
            referencedColumnName = "dom_per_id",
            insertable = false,
            updatable = false
        ), JoinColumn(name = "chs_doc_id", referencedColumnName = "dom_doc_id", insertable = false, updatable = false)
    )
    var domicilio: Domicilio? = null,

    @OneToOne
    @JoinColumn(name = "chs_lec_id", insertable = false, updatable = false)
    var lectivo: Lectivo? = null,

    @OneToOne
    @JoinColumn(name = "chs_art_id", insertable = false, updatable = false)
    var arancelTipo: ArancelTipo? = null,

    @OneToOne
    @JoinColumn(name = "chs_geo_id", insertable = false, updatable = false)
    var geografica: Geografica? = null

) : Auditable()
