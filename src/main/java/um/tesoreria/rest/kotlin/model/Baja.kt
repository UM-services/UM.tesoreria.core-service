package um.tesoreria.rest.kotlin.model

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinColumns
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["baj_fac_id", "baj_tch_id", "baj_chs_id"])])
data class Baja(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var bajaId: Long? = null,

    @Column(name = "baj_fac_id")
    var facultadId: Int? = null,

    @Column(name = "baj_tch_id")
    var tipoChequeraId: Int? = null,

    @Column(name = "baj_chs_id")
    var chequeraSerieId: Long? = null,

    var chequeraId: Long? = null,

    @Column(name = "baj_lec_id")
    var lectivoId: Int? = null,

    @Column(name = "baj_per_id")
    var personaId: BigDecimal? = null,

    @Column(name = "baj_doc_id")
    var documentoId: Int? = null,

    @Column(name = "baj_fecha")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fecha: OffsetDateTime? = null,

    @Column(name = "baj_observaciones")
    var observaciones: String? = null,

    var egresado: Byte = 0,

    @OneToOne(optional = true)
    @JoinColumn(name = "baj_fac_id", insertable = false, updatable = false)
    var facultad: Facultad? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "baj_tch_id", insertable = false, updatable = false)
    var tipoChequera: TipoChequera? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "baj_lec_id", insertable = false, updatable = false)
    var lectivo: Lectivo? = null,

    @OneToOne(optional = true)
    @JoinColumns(
        JoinColumn(name = "baj_per_id", referencedColumnName = "per_id", insertable = false, updatable = false),
        JoinColumn(name = "baj_doc_id", referencedColumnName = "per_doc_id", insertable = false, updatable = false)
    )
    var persona: Persona? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "chequeraId", insertable = false, updatable = false)
    var chequeraSerie: ChequeraSerie? = null,

    ) : Auditable()
