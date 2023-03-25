package ar.edu.um.tesoreria.rest.kotlin.model

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["dom_per_id", "dom_doc_id"])])
data class Domicilio(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dom_id")
    var domicilioId: Long? = null,

    @Column(name = "dom_per_id")
    var personaId: BigDecimal? = null,

    @Column(name = "dom_doc_id")
    var documentoId: Int? = null,

    @Column(name = "dom_fecha")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fecha: OffsetDateTime? = OffsetDateTime.now(),

    @Column(name = "dom_calle")
    var calle: String = "",

    @Column(name = "dom_puerta")
    var puerta: String = "",

    @Column(name = "dom_piso")
    var piso: String = "",

    @Column(name = "dom_dpto")
    var dpto: String = "",

    @Column(name = "dom_telefono")
    var telefono: String = "",

    @Column(name = "dom_movil")
    var movil: String = "",

    @Column(name = "dom_observ")
    var observaciones: String = "",

    @Column(name = "dom_codpostal")
    var codigoPostal: String = "",

    @Column(name = "dom_fac_id")
    var facultadId: Int? = null,

    @Column(name = "dom_prv_id")
    var provinciaId: Int? = null,

    @Column(name = "dom_loc_id")
    var localidadId: Int? = null,

    @Column(name = "dom_e_mail")
    var emailPersonal: String = "",

    @Column(name = "mail_institucional")
    var emailInstitucional: String = "",

    @Column(name = "dom_laboral")
    var laboral: String = "",

    )
