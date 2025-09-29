package um.tesoreria.core.kotlin.model

import um.tesoreria.core.model.Proveedor
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.persistence.Id
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Column
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.OffsetDateTime
import java.math.BigDecimal
import jakarta.persistence.OneToOne
import jakarta.persistence.JoinColumn
import um.tesoreria.core.util.Jsonifier

@Entity
@Table(
    name = "movcon",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["mco_fecha", "mco_nrocomp", "mco_item"])
    ]
)
data class  CuentaMovimiento(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var cuentaMovimientoId: Long? = null,

    @Column(name = "mco_fecha")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaContable: OffsetDateTime? = null,

    @Column(name = "mco_nrocomp")
    var ordenContable: Int = 0,

    @Column(name = "mco_item")
    var item: Int = 0,

    @Column(name = "mco_pla_cuenta")
    var numeroCuenta: BigDecimal? = null,

    @Column(name = "mco_debita")
    var debita: Byte = 0,

    @Column(name = "mco_tco_id")
    var comprobanteId: Int? = null,

    @Column(name = "mco_concepto")
    var concepto: String = "",

    @Column(name = "mco_importe")
    var importe: BigDecimal = BigDecimal.ZERO,

    @Column(name = "mco_prv_id")
    var proveedorId: Int? = null,

    @Column(name = "mco_nroanulado")
    var numeroAnulado: Int = 0,

    var version: Int = 0,

    @Column(name = "proveedormovimiento_id")
    var proveedorMovimientoId: Long? = null,

    @Column(name = "proveedormovimiento_id_orden_pago")
    var proveedorMovimientoIdOrdenPago: Long? = null,

    var apertura: Byte = 0,

    var trackId: Long? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "mco_pla_cuenta", insertable = false, updatable = false)
    var cuenta: Cuenta? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "mco_prv_id", insertable = false, updatable = false)
    var proveedor: Proveedor? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "mco_tco_id", insertable = false, updatable = false)
    var comprobante: Comprobante? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "proveedormovimiento_id", insertable = false, updatable = false)
    var proveedorMovimiento: ProveedorMovimiento? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "proveedormovimiento_id_orden_pago", insertable = false, updatable = false)
    var ordenPago: ProveedorMovimiento? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "trackId", insertable = false, updatable = false)
    var track: Track? = null

) : Auditable() {
    fun jsonify(): String {
        return Jsonifier.builder(this).build()
    }

    companion object {
        fun builder() = Builder()
    }

    class Builder {
        private var cuentaMovimientoId: Long? = null
        private var fechaContable: OffsetDateTime? = null
        private var ordenContable: Int = 0
        private var item: Int = 0
        private var numeroCuenta: BigDecimal? = null
        private var debita: Byte = 0
        private var comprobanteId: Int? = null
        private var concepto: String = ""
        private var importe: BigDecimal = BigDecimal.ZERO
        private var proveedorId: Int? = null
        private var numeroAnulado: Int = 0
        private var version: Int = 0
        private var proveedorMovimientoId: Long? = null
        private var proveedorMovimientoIdOrdenPago: Long? = null
        private var apertura: Byte = 0
        private var trackId: Long? = null
        private var cuenta: Cuenta? = null
        private var proveedor: Proveedor? = null
        private var comprobante: Comprobante? = null
        private var proveedorMovimiento: ProveedorMovimiento? = null
        private var ordenPago: ProveedorMovimiento? = null
        private var track: Track? = null

        fun cuentaMovimientoId(cuentaMovimientoId: Long?) = apply { this.cuentaMovimientoId = cuentaMovimientoId }
        fun fechaContable(fechaContable: OffsetDateTime?) = apply { this.fechaContable = fechaContable }
        fun ordenContable(ordenContable: Int) = apply { this.ordenContable = ordenContable }
        fun item(item: Int) = apply { this.item = item }
        fun numeroCuenta(numeroCuenta: BigDecimal?) = apply { this.numeroCuenta = numeroCuenta }
        fun debita(debita: Byte) = apply { this.debita = debita }
        fun comprobanteId(comprobanteId: Int?) = apply { this.comprobanteId = comprobanteId }
        fun concepto(concepto: String) = apply { this.concepto = concepto }
        fun importe(importe: BigDecimal) = apply { this.importe = importe }
        fun proveedorId(proveedorId: Int?) = apply { this.proveedorId = proveedorId }
        fun numeroAnulado(numeroAnulado: Int) = apply { this.numeroAnulado = numeroAnulado }
        fun version(version: Int) = apply { this.version = version }
        fun proveedorMovimientoId(proveedorMovimientoId: Long?) =
            apply { this.proveedorMovimientoId = proveedorMovimientoId }

        fun proveedorMovimientoIdOrdenPago(proveedorMovimientoIdOrdenPago: Long?) =
            apply { this.proveedorMovimientoIdOrdenPago = proveedorMovimientoIdOrdenPago }

        fun apertura(apertura: Byte) = apply { this.apertura = apertura }
        fun trackId(trackId: Long?) = apply { this.trackId = trackId }
        fun cuenta(cuenta: Cuenta?) = apply { this.cuenta = cuenta }
        fun proveedor(proveedor: Proveedor?) = apply { this.proveedor = proveedor }
        fun comprobante(comprobante: Comprobante?) = apply { this.comprobante = comprobante }
        fun proveedorMovimiento(proveedorMovimiento: ProveedorMovimiento?) =
            apply { this.proveedorMovimiento = proveedorMovimiento }

        fun ordenPago(ordenPago: ProveedorMovimiento?) = apply { this.ordenPago = ordenPago }
        fun track(track: Track?) = apply { this.track = track }

        fun build() = CuentaMovimiento(
            cuentaMovimientoId,
            fechaContable,
            ordenContable,
            item,
            numeroCuenta,
            debita,
            comprobanteId,
            concepto,
            importe,
            proveedorId,
            numeroAnulado,
            version,
            proveedorMovimientoId,
            proveedorMovimientoIdOrdenPago,
            apertura,
            trackId,
            cuenta,
            proveedor,
            comprobante,
            proveedorMovimiento,
            ordenPago,
            track
        )
    }
}