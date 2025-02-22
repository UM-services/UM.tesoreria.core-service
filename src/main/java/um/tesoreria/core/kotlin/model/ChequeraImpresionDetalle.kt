package um.tesoreria.core.kotlin.model

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(name = "chequera_d_impresion")
data class ChequeraImpresionDetalle(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var chequeraImpresionDetalleId: Long? = null,

    var chequeraImpresionCabeceraId: Long? = null,
    var facultadId: Int? = null,

    @Column(name = "tipochequera_id")
    var tipoChequeraId: Int? = null,

    @Column(name = "chequeraserie_id")
    var chequeraSerieId: Long? = null,

    var productoId: Int? = null,
    var alternativaId: Int? = null,
    var cuotaId: Int? = null,
    var titulo: String = "",
    var total: BigDecimal = BigDecimal.ZERO,
    var cuotas: Int = 0,
    var mes: Int = 0,
    var anho: Int = 0,
    var arancelTipoId: Int? = null,

    @Column(name = "vencimiento_1")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var vencimiento1: OffsetDateTime? = null,

    @Column(name = "importe_1")
    var importe1: BigDecimal = BigDecimal.ZERO,

    @Column(name = "vencimiento_2")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var vencimiento2: OffsetDateTime? = null,

    @Column(name = "importe_2")
    var importe2: BigDecimal = BigDecimal.ZERO,

    @Column(name = "vencimiento_3")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var vencimiento3: OffsetDateTime? = null,

    @Column(name = "importe_3")
    var importe3: BigDecimal = BigDecimal.ZERO,

    var usuarioId: String? = null,
    var version: Long? = null

) : Auditable()
