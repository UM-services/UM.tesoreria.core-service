package um.tesoreria.rest.kotlin.model.view

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.Immutable
import um.tesoreria.rest.kotlin.model.*
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(
    name = "vw_chequera_cuota_deuda",
    uniqueConstraints = [UniqueConstraint(columnNames = ["chc_fac_id", "chc_tch_id", "chc_chs_id", "chc_pro_id", "chc_alt_id", "chc_cuo_id"])]
)
@Immutable
data class ChequeraCuotaDeuda(

    @Id
    @Column(name = "chc_id")
    var chequeraCuotaId: Long? = null,

    var chequeraId: Long? = null,

    @Column(name = "chc_fac_id")
    var facultadId: Int? = null,

    @Column(name = "chc_tch_id")
    var tipoChequeraId: Int? = null,

    @Column(name = "chc_chs_id")
    var chequeraSerieId: Long? = null,

    @Column(name = "chc_pro_id")
    var productoId: Int? = null,

    @Column(name = "chc_alt_id")
    var alternativaId: Int? = null,

    @Column(name = "chc_cuo_id")
    var cuotaId: Int? = null,

    @Column(name = "chc_mes")
    var mes: Int = 0,

    @Column(name = "chc_anio")
    var anho: Int = 0,

    var arancelTipoId: Long? = null,

    @Column(name = "chc_1er_vencimiento")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var vencimiento1: OffsetDateTime? = null,

    @Column(name = "chc_1er_importe")
    var importe1: BigDecimal = BigDecimal.ZERO,

    @Column(name = "importe1_original")
    var importe1Original: BigDecimal = BigDecimal.ZERO,

    @Column(name = "chc_2do_vencimiento")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var vencimiento2: OffsetDateTime? = null,

    @Column(name = "chc_2do_importe")
    var importe2: BigDecimal = BigDecimal.ZERO,

    @Column(name = "importe2_original")
    var importe2Original: BigDecimal = BigDecimal.ZERO,

    @Column(name = "chc_3er_vencimiento")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var vencimiento3: OffsetDateTime? = null,

    @Column(name = "chc_3er_importe")
    var importe3: BigDecimal = BigDecimal.ZERO,

    @Column(name = "importe3_original")
    var importe3Original: BigDecimal = BigDecimal.ZERO,

    @Column(name = "chc_codigo_barras")
    var codigoBarras: String = "",

    @Column(name = "chc_i2of5")
    var i2Of5: String = "",

    @Column(name = "chc_pagado")
    var pagado: Byte = 0,

    @Column(name = "chc_baja")
    var baja: Byte = 0,

    @Column(name = "chc_manual")
    var manual: Byte = 0,

    @Column(name = "chc_tra_id")
    var tramoId: Int = 0,

    @OneToOne(optional = true)
    @JoinColumn(name = "chc_pro_id", insertable = false, updatable = false)
    var producto: Producto? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "chequeraId", referencedColumnName = "clave", insertable = false, updatable = false)
    var chequeraSerie: ChequeraSerie? = null,

    @OneToOne
    @JoinColumn(name = "chc_id", insertable = false, updatable = false)
    @JsonIgnore
    var chequeraCuota: ChequeraCuota? = null

)
