package um.tesoreria.core.kotlin.model

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["ccr_fac_id", "ccr_tch_id", "ccr_chs_id", "ccr_pro_id", "ccr_alt_id", "ccr_cuo_id"])])
data class ChequeraCuotaReemplazo(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clave")
    var chequeraCuotaReemplazoId: Long? = null,

    @Column(name = "ccr_fac_id")
    var facultadId: Int? = null,

    @Column(name = "ccr_tch_id")
    var tipoChequeraId: Int? = null,

    @Column(name = "ccr_chs_id")
    var chequeraSerieId: Long? = null,

    @Column(name = "ccr_pro_id")
    var productoId: Int? = null,

    @Column(name = "ccr_alt_id")
    var alternativaId: Int? = null,

    @Column(name = "ccr_cuo_id")
    var cuotaId: Int? = null,

    @Column(name = "ccr_mes")
    var mes: Int = 0,

    @Column(name = "ccr_anio")
    var anho: Int = 0,

    @Column(name = "arancel_tipo_id")
    var arancelTipoId: Int? = null,

    @Column(name = "ccr_1er_Vencimiento")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var vencimiento1: OffsetDateTime? = null,

    @Column(name = "ccr_1er_Importe")
    var importe1: BigDecimal = BigDecimal.ZERO,

    @Column(name = "importe1_original")
    var importe1Original: BigDecimal = BigDecimal.ZERO,

    @Column(name = "ccr_2do_Vencimiento")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var vencimiento2: OffsetDateTime? = null,

    @Column(name = "ccr_2do_Importe")
    var importe2: BigDecimal = BigDecimal.ZERO,

    @Column(name = "importe2_original")
    var importe2Original: BigDecimal = BigDecimal.ZERO,

    @Column(name = "ccr_3er_Vencimiento")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var vencimiento3: OffsetDateTime? = null,

    @Column(name = "ccr_3er_Importe")
    var importe3: BigDecimal = BigDecimal.ZERO,

    @Column(name = "importe3_original")
    var importe3Original: BigDecimal = BigDecimal.ZERO,

    @Column(name = "ccr_Codigo_Barras")
    var codigoBarras: String = "",

    @Column(name = "ccr_I2of5")
    var i2Of5: String = "",

    @Column(name = "ccr_Pagado")
    var pagado: Byte = 0,

    @Column(name = "ccr_Baja")
    var baja: Byte = 0,

    @Column(name = "ccr_Manual")
    var manual: Byte = 0,

    @Column(name = "ccr_Tra_ID")
    var tramoId: Int? = null,

    var compensada: Byte = 0,

    @OneToOne(optional = true)
    @JoinColumn(name = "ccr_fac_id", insertable = false, updatable = false)
    var facultad: Facultad? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "ccr_tch_id", insertable = false, updatable = false)
    var tipoChequera: TipoChequera? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "ccr_pro_id", insertable = false, updatable = false)
    var producto: Producto? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "ccr_chs_id", referencedColumnName = "clave", insertable = false, updatable = false)
    var chequeraSerie: ChequeraSerie? = null

) : Auditable()
