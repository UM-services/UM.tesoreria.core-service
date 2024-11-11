package um.tesoreria.core.kotlin.model

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["chc_fac_id", "chc_tch_id", "chc_chs_id", "chc_pro_id", "chc_alt_id", "chc_cuo_id"])])
data class ChequeraCuota(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    var arancelTipoId: Int? = null,

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

    var compensada: Byte = 0,

    @Column(name = "chc_tra_id")
    var tramoId: Int = 0,

    @OneToOne(optional = true)
    @JoinColumn(name = "chc_fac_id", insertable = false, updatable = false)
    var facultad: Facultad? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "chc_tch_id", insertable = false, updatable = false)
    var tipoChequera: TipoChequera? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "chc_pro_id", insertable = false, updatable = false)
    var producto: Producto? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "chequeraId", referencedColumnName = "clave", insertable = false, updatable = false)
    var chequeraSerie: ChequeraSerie? = null

) : Auditable() {

    fun cuotaKey(): String {
        return (this.facultadId.toString() + "." + this.tipoChequeraId + "." + this.chequeraSerieId + "." + this.productoId + "."
                + this.alternativaId + "." + this.cuotaId)
    }

    companion object {
        fun builder(): Builder = Builder()
    }

    class Builder {
        private var chequeraCuotaId: Long? = null
        private var chequeraId: Long? = null
        private var facultadId: Int? = null
        private var tipoChequeraId: Int? = null
        private var chequeraSerieId: Long? = null
        private var productoId: Int? = null
        private var alternativaId: Int? = null
        private var cuotaId: Int? = null
        private var mes: Int = 0
        private var anho: Int = 0
        private var arancelTipoId: Int? = null
        private var vencimiento1: OffsetDateTime? = null
        private var importe1: BigDecimal = BigDecimal.ZERO
        private var importe1Original: BigDecimal = BigDecimal.ZERO
        private var vencimiento2: OffsetDateTime? = null
        private var importe2: BigDecimal = BigDecimal.ZERO
        private var importe2Original: BigDecimal = BigDecimal.ZERO
        private var vencimiento3: OffsetDateTime? = null
        private var importe3: BigDecimal = BigDecimal.ZERO
        private var importe3Original: BigDecimal = BigDecimal.ZERO
        private var codigoBarras: String = ""
        private var i2Of5: String = ""
        private var pagado: Byte = 0
        private var baja: Byte = 0
        private var manual: Byte = 0
        private var compensada: Byte = 0
        private var tramoId: Int = 0
        private var facultad: Facultad? = null
        private var tipoChequera: TipoChequera? = null
        private var producto: Producto? = null
        private var chequeraSerie: ChequeraSerie? = null

        fun chequeraCuotaId(chequeraCuotaId: Long?) = apply { this.chequeraCuotaId = chequeraCuotaId }
        fun chequeraId(chequeraId: Long?) = apply { this.chequeraId = chequeraId }
        fun facultadId(facultadId: Int?) = apply { this.facultadId = facultadId }
        fun tipoChequeraId(tipoChequeraId: Int?) = apply { this.tipoChequeraId = tipoChequeraId }
        fun chequeraSerieId(chequeraSerieId: Long?) = apply { this.chequeraSerieId = chequeraSerieId }
        fun productoId(productoId: Int?) = apply { this.productoId = productoId }
        fun alternativaId(alternativaId: Int?) = apply { this.alternativaId = alternativaId }
        fun cuotaId(cuotaId: Int?) = apply { this.cuotaId = cuotaId }
        fun mes(mes: Int) = apply { this.mes = mes }
        fun anho(anho: Int) = apply { this.anho = anho }
        fun arancelTipoId(arancelTipoId: Int?) = apply { this.arancelTipoId = arancelTipoId }
        fun vencimiento1(vencimiento1: OffsetDateTime?) = apply { this.vencimiento1 = vencimiento1 }
        fun importe1(importe1: BigDecimal) = apply { this.importe1 = importe1 }
        fun importe1Original(importe1Original: BigDecimal) = apply { this.importe1Original = importe1Original }
        fun vencimiento2(vencimiento2: OffsetDateTime?) = apply { this.vencimiento2 = vencimiento2 }
        fun importe2(importe2: BigDecimal) = apply { this.importe2 = importe2 }
        fun importe2Original(importe2Original: BigDecimal) = apply { this.importe2Original = importe2Original }
        fun vencimiento3(vencimiento3: OffsetDateTime?) = apply { this.vencimiento3 = vencimiento3 }
        fun importe3(importe3: BigDecimal) = apply { this.importe3 = importe3 }
        fun importe3Original(importe3Original: BigDecimal) = apply { this.importe3Original = importe3Original }
        fun codigoBarras(codigoBarras: String) = apply { this.codigoBarras = codigoBarras }
        fun i2Of5(i2Of5: String) = apply { this.i2Of5 = i2Of5 }
        fun pagado(pagado: Byte) = apply { this.pagado = pagado }
        fun baja(baja: Byte) = apply { this.baja = baja }
        fun manual(manual: Byte) = apply { this.manual = manual }
        fun compensada(compensada: Byte) = apply { this.compensada = compensada }
        fun tramoId(tramoId: Int) = apply { this.tramoId = tramoId }
        fun facultad(facultad: Facultad?) = apply { this.facultad = facultad }
        fun tipoChequera(tipoChequera: TipoChequera?) = apply { this.tipoChequera = tipoChequera }
        fun producto(producto: Producto?) = apply { this.producto = producto }
        fun chequeraSerie(chequeraSerie: ChequeraSerie?) = apply { this.chequeraSerie = chequeraSerie }

        fun build() = ChequeraCuota(
            chequeraCuotaId = chequeraCuotaId,
            chequeraId = chequeraId,
            facultadId = facultadId,
            tipoChequeraId = tipoChequeraId,
            chequeraSerieId = chequeraSerieId,
            productoId = productoId,
            alternativaId = alternativaId,
            cuotaId = cuotaId,
            mes = mes,
            anho = anho,
            arancelTipoId = arancelTipoId,
            vencimiento1 = vencimiento1,
            importe1 = importe1,
            importe1Original = importe1Original,
            vencimiento2 = vencimiento2,
            importe2 = importe2,
            importe2Original = importe2Original,
            vencimiento3 = vencimiento3,
            importe3 = importe3,
            importe3Original = importe3Original,
            codigoBarras = codigoBarras,
            i2Of5 = i2Of5,
            pagado = pagado,
            baja = baja,
            manual = manual,
            compensada = compensada,
            tramoId = tramoId,
            facultad = facultad,
            tipoChequera = tipoChequera,
            producto = producto,
            chequeraSerie = chequeraSerie
        )
    }

}
