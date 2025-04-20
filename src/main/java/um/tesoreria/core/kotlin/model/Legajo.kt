package um.tesoreria.core.kotlin.model

import java.math.BigDecimal
import java.time.OffsetDateTime
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinColumns
import jakarta.persistence.OneToOne

@Entity
@Table(
    name = "aluleg",
    uniqueConstraints = [UniqueConstraint(columnNames = ["ale_fac_id", "ale_per_id", "ale_doc_id"])]
)
data class Legajo(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ale_id")
    var legajoId: Long? = null,

    @Column(name = "ale_per_id")
    var personaId: BigDecimal? = null,

    @Column(name = "ale_doc_id")
    var documentoId: Int? = null,

    @Column(name = "ale_fac_id")
    var facultadId: Int? = null,

    @Column(name = "ale_leg_id")
    var numeroLegajo: Long = 0L,

    @Column(name = "ale_fecha")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fecha: OffsetDateTime? = null,

    @Column(name = "ale_lec_id")
    var lectivoId: Int? = null,

    @Column(name = "ale_pla_id")
    var planId: Int? = null,

    @Column(name = "ale_car_id")
    var carreraId: Int? = null,

    @Column(name = "ale_carrera")
    var tieneCarrera: Byte = 0,

    @Column(name = "ale_geo_id")
    var geograficaId: Int? = null,

    @Column(name = "ale_contrasenia")
    var contrasenha: String? = null,

    @Column(name = "intercambio")
    var intercambio: Byte = 0,

    @OneToOne(optional = true)
    @JoinColumns(
        JoinColumn(name = "ale_fac_id", referencedColumnName = "car_fac_id", insertable = false, updatable = false),
        JoinColumn(name = "ale_pla_id", referencedColumnName = "car_pla_id", insertable = false, updatable = false),
        JoinColumn(name = "ale_car_id", referencedColumnName = "car_id", insertable = false, updatable = false)
    )
    var carrera: Carrera? = null

) : Auditable()
