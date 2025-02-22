package um.tesoreria.core.kotlin.model

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.Column
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

    @Column(name = "tipochequera_id")
    var tipoChequeraId: Int? = null,

    @Column(name = "chequeraserie_id")
    var chequeraSerieId: Long? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fecha: OffsetDateTime? = null,

    var personaId: BigDecimal? = null,
    var documentoId: Int? = null,
    var lectivoId: Int? = null,
    var geograficaId: Int? = null,

    @Column(name = "aranceltipo_id")
    var arancelTipoId: Int? = null,

    var alternativaId: Int? = null,
    var usuarioId: String? = null,
    var version: Long? = null

) : Auditable()
