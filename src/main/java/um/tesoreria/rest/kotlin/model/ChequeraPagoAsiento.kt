package um.tesoreria.rest.kotlin.model

import jakarta.persistence.*
import java.math.BigDecimal

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
    var chequerapagoasientoId: Long? = null,

    @Column(name = "cpa_fac_id")
    var facultadId: Int? = null,

    @Column(name = "cpa_tch_id")
    var tipochequeraId: Int? = null,

    @Column(name = "cpa_chs_id")
    var chequeraserieId: Long? = null,

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
    var importe: BigDecimal = BigDecimal.ZERO

) : Auditable()
