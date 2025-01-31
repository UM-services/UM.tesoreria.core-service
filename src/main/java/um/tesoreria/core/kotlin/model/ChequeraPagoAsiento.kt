package um.tesoreria.core.kotlin.model

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import um.tesoreria.core.model.ChequeraPagoReemplazo
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(
    uniqueConstraints = [UniqueConstraint(
        columnNames = ["cpa_fac_id", "cpa_tch_id", "cpa_chs_id", "cpa_pro_id", "cpa_alt_id", "cpa_cuo_id", "cpa_orden", "cpa_item"]
    )]
)
data class ChequeraPagoAsiento(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clave")
    var chequeraPagoAsientoId: Long? = null,

    var chequeraPagoId: Long? = null,
    var chequeraPagoReemplazoId: Long? = null,
    var tipoPagoId: Int? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fecha: OffsetDateTime? = null,

    @Column(name = "cpa_fac_id")
    var facultadId: Int? = null,

    @Column(name = "cpa_tch_id")
    var tipoChequeraId: Int? = null,

    @Column(name = "cpa_chs_id")
    var chequeraSerieId: Long? = null,

    @Column(name = "cpa_pro_id")
    var productoId: Int? = null,

    @Column(name = "cpa_alt_id")
    var alternativaId: Int? = null,

    @Column(name = "cpa_cuo_id")
    var cuotaId: Int? = null,

    @Column(name = "cpa_orden")
    var orden: Int? = null,

    @Column(name = "cpa_item")
    var item: Int? = null,

    @Column(name = "cpa_cuenta")
    var cuenta: BigDecimal? = null,

    @Column(name = "cpa_debita")
    var debita: Byte = 0,

    @Column(name = "cpa_importe")
    var importe: BigDecimal = BigDecimal.ZERO,

    @OneToOne(optional = true)
    @JoinColumn(name = "chequeraPagoId", referencedColumnName = "clave", updatable = false, insertable = false)
    var chequeraPago: ChequeraPago? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "chequeraPagoReemplazoId", referencedColumnName = "clave", updatable = false, insertable = false)
    var chequeraPagoReemplazo: ChequeraPagoReemplazo? = null

) : Auditable()
