package ar.edu.um.tesoreria.rest.kotlin.model

import ar.edu.um.tesoreria.rest.model.Auditable
import ar.edu.um.tesoreria.rest.model.Geografica
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["chs_fac_id", "chs_tch_id", "chs_id"])])
data class ChequeraSerie(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    var asentado: Byte? = null,

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
    var flagPayperTic: Byte = 0,

    var usuarioId: String? = null,
    var enviado: Byte = 0,
    var retenida: Byte = 0,

    @Transient
    var cuotasDeuda: Int = 0,

    @Transient
    var importeDeuda: BigDecimal = BigDecimal.ZERO,

    @OneToOne(optional = true)
    @JoinColumn(name = "chs_fac_id", insertable = false, updatable = false)
    var facultad: Facultad? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "chs_tch_id", insertable = false, updatable = false)
    var tipoChequera: TipoChequera? = null,

    @OneToOne(optional = true)
    @JoinColumns(
        JoinColumn(name = "chs_per_id", referencedColumnName = "per_id", insertable = false, updatable = false),
        JoinColumn(name = "chs_doc_id", referencedColumnName = "per_doc_id", insertable = false, updatable = false)
    )
    var persona: Persona? = null,

    @OneToOne(optional = true)
    @JoinColumns(
        JoinColumn(
            name = "chs_per_id",
            referencedColumnName = "dom_per_id",
            insertable = false,
            updatable = false
        ), JoinColumn(name = "chs_doc_id", referencedColumnName = "dom_doc_id", insertable = false, updatable = false)
    )
    var domicilio: Domicilio? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "chs_lec_id", insertable = false, updatable = false)
    var lectivo: Lectivo? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "chs_art_id", insertable = false, updatable = false)
    var arancelTipo: ArancelTipo? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "chs_geo_id", insertable = false, updatable = false)
    var geografica: Geografica? = null,

    ) : Auditable() {
    fun getPersonaKey(): String {
        return this.personaId.toString() + "." + this.documentoId
    }

    fun getFacultadKey(): String {
        return this.facultadId.toString() + "." + this.lectivoId + "." + this.geograficaId + "." + this.getPersonaKey()
    }

}
