package um.tesoreria.core.kotlin.model

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["chp_fac_id", "chp_tch_id", "chp_chs_id", "chp_pro_id", "chp_alt_id", "chp_cuo_id", "chp_orden"])])
data class ChequeraPago(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clave")
    var chequeraPagoId: Long? = null,

    var chequeraCuotaId: Long? = null,

    @Column(name = "chp_fac_id")
    var facultadId: Int? = null,

    @Column(name = "chp_tch_id")
    var tipoChequeraId: Int? = null,

    @Column(name = "chp_chs_id")
    var chequeraSerieId: Long? = null,

    @Column(name = "chp_pro_id")
    var productoId: Int? = null,

    @Column(name = "chp_alt_id")
    var alternativaId: Int? = null,

    @Column(name = "chp_cuo_id")
    var cuotaId: Int? = null,

    @Column(name = "chp_orden")
    var orden: Int? = null,

    @Column(name = "chp_mes")
    var mes: Int = 0,

    @Column(name = "chp_anio")
    var anho: Int = 0,

    @Column(name = "chp_fecha")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fecha: OffsetDateTime? = null,

    @Column(name = "chp_acred")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var acreditacion: OffsetDateTime? = null,

    @Column(name = "chp_importe")
    var importe: BigDecimal = BigDecimal.ZERO,

    @Column(name = "chp_path")
    var path: String = "",

    @Column(name = "chp_archivo")
    var archivo: String = "",

    @Column(name = "chp_observaciones")
    var observaciones: String = "",

    @Column(name = "chp_arb_id")
    var archivoBancoId: Long? = null,

    @Column(name = "chp_arb_id_acred")
    var archivoBancoIdAcreditacion: Long? = null,

    var verificador: Int = 0,

    @Column(name = "chp_tpa_id")
    var tipoPagoId: Int? = null,

    var idMercadoPago: String? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "chp_tpa_id", insertable = false, updatable = false)
    var tipoPago: TipoPago? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "chp_pro_id", insertable = false, updatable = false)
    var producto: Producto? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "chequeraCuotaId", referencedColumnName = "chc_id", insertable = false, updatable = false)
    var chequeraCuota: ChequeraCuota? = null

) : Auditable() {

    fun getCuotaKey(): String {
        return (this.facultadId.toString() + "." + this.tipoChequeraId + "." + this.chequeraSerieId + "." + this.productoId + "."
                + this.alternativaId + "." + this.cuotaId)
    }

    companion object {
        fun builder(): Builder = Builder()
    }

    class Builder {
        private var chequeraPagoId: Long? = null
        private var chequeraCuotaId: Long? = null
        private var facultadId: Int? = null
        private var tipoChequeraId: Int? = null
        private var chequeraSerieId: Long? = null
        private var productoId: Int? = null
        private var alternativaId: Int? = null
        private var cuotaId: Int? = null
        private var orden: Int? = null
        private var mes: Int = 0
        private var anho: Int = 0
        private var fecha: OffsetDateTime? = null
        private var acreditacion: OffsetDateTime? = null
        private var importe: BigDecimal = BigDecimal.ZERO
        private var path: String = ""
        private var archivo: String = ""
        private var observaciones: String = ""
        private var archivoBancoId: Long? = null
        private var archivoBancoIdAcreditacion: Long? = null
        private var verificador: Int = 0
        private var tipoPagoId: Int? = null
        private var idMercadoPago: String? = null
        private var tipoPago: TipoPago? = null
        private var producto: Producto? = null
        private var chequeraCuota: ChequeraCuota? = null

        fun chequeraPagoId(chequeraPagoId: Long?) = apply { this.chequeraPagoId = chequeraPagoId }
        fun chequeraCuotaId(chequeraCuotaId: Long?) = apply { this.chequeraCuotaId = chequeraCuotaId }
        fun facultadId(facultadId: Int?) = apply { this.facultadId = facultadId }
        fun tipoChequeraId(tipoChequeraId: Int?) = apply { this.tipoChequeraId = tipoChequeraId }
        fun chequeraSerieId(chequeraSerieId: Long?) = apply { this.chequeraSerieId = chequeraSerieId }
        fun productoId(productoId: Int?) = apply { this.productoId = productoId }
        fun alternativaId(alternativaId: Int?) = apply { this.alternativaId = alternativaId }
        fun cuotaId(cuotaId: Int?) = apply { this.cuotaId = cuotaId }
        fun orden(orden: Int?) = apply { this.orden = orden }
        fun mes(mes: Int) = apply { this.mes = mes }
        fun anho(anho: Int) = apply { this.anho = anho }
        fun fecha(fecha: OffsetDateTime?) = apply { this.fecha = fecha }
        fun acreditacion(acreditacion: OffsetDateTime?) = apply { this.acreditacion = acreditacion }
        fun importe(importe: BigDecimal) = apply { this.importe = importe }
        fun path(path: String) = apply { this.path = path }
        fun archivo(archivo: String) = apply { this.archivo = archivo }
        fun observaciones(observaciones: String) = apply { this.observaciones = observaciones }
        fun archivoBancoId(archivoBancoId: Long?) = apply { this.archivoBancoId = archivoBancoId }
        fun archivoBancoIdAcreditacion(archivoBancoIdAcreditacion: Long?) = apply { this.archivoBancoIdAcreditacion = archivoBancoIdAcreditacion }
        fun verificador(verificador: Int) = apply { this.verificador = verificador }
        fun tipoPagoId(tipoPagoId: Int?) = apply { this.tipoPagoId = tipoPagoId }
        fun idMercadoPago(idMercadoPago: String?) = apply { this.idMercadoPago = idMercadoPago }
        fun tipoPago(tipoPago: TipoPago?) = apply { this.tipoPago = tipoPago }
        fun producto(producto: Producto?) = apply { this.producto = producto }
        fun chequeraCuota(chequeraCuota: ChequeraCuota?) = apply { this.chequeraCuota = chequeraCuota }

        fun build() = ChequeraPago(
            chequeraPagoId = chequeraPagoId,
            chequeraCuotaId = chequeraCuotaId,
            facultadId = facultadId,
            tipoChequeraId = tipoChequeraId,
            chequeraSerieId = chequeraSerieId,
            productoId = productoId,
            alternativaId = alternativaId,
            cuotaId = cuotaId,
            orden = orden,
            mes = mes,
            anho = anho,
            fecha = fecha,
            acreditacion = acreditacion,
            importe = importe,
            path = path,
            archivo = archivo,
            observaciones = observaciones,
            archivoBancoId = archivoBancoId,
            archivoBancoIdAcreditacion = archivoBancoIdAcreditacion,
            verificador = verificador,
            tipoPagoId = tipoPagoId,
            idMercadoPago = idMercadoPago,
            tipoPago = tipoPago,
            producto = producto,
            chequeraCuota = chequeraCuota
        )
    }
}
