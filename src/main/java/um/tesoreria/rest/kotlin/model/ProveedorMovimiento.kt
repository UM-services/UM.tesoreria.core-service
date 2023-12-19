package um.tesoreria.rest.kotlin.model

import um.tesoreria.rest.model.Proveedor
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(name = "movprov")
data class ProveedorMovimiento(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mvp_id")
    var proveedorMovimientoId: Long? = null,

    @Column(name = "mvp_prv_id")
    var proveedorId: Int? = null,

    @Column(name = "mvp_nombrebeneficiario")
    var nombreBeneficiario: String = "",

    @Column(name = "mvp_tco_id")
    var comprobanteId: Int? = null,

    @Column(name = "mvp_fechacomprob")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaComprobante: OffsetDateTime? = null,

    @Column(name = "mvp_fechavenc")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaVencimiento: OffsetDateTime? = null,

    @Column(name = "mvp_prefijo")
    var prefijo: Int = 0,

    @Column(name = "mvp_nrocomprob")
    var numeroComprobante: Long = 0L,

    @Column(name = "mvp_netosindescuento")
    var netoSinDescuento: BigDecimal = BigDecimal.ZERO,

    @Column(name = "mvp_descuento")
    var descuento: BigDecimal = BigDecimal.ZERO,

    @Column(name = "mvp_neto")
    var neto: BigDecimal = BigDecimal.ZERO,

    @Column(name = "mvp_importe")
    var importe: BigDecimal = BigDecimal.ZERO,

    @Column(name = "mvp_cancelado")
    var cancelado: BigDecimal = BigDecimal.ZERO,

    @Column(name = "mvp_fechareg")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaContable: OffsetDateTime? = null,

    @Column(name = "mvp_nrocomp")
    var ordenContable: Int? = null,

    @Column(name = "mvp_concepto")
    var concepto: String? = null,

    @Column(name = "mvp_anulado")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaAnulacion: OffsetDateTime? = null,

    @Column(name = "mvp_concargo")
    var conCargo: Byte = 0,

    @Column(name = "mvp_solicfactura")
    var solicitaFactura: Byte = 0,

    var geograficaId: Int? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "mvp_tco_id", insertable = false, updatable = false)
    var comprobante: Comprobante? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "mvp_prv_id", insertable = false, updatable = false)
    var proveedor: Proveedor? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "geograficaId", insertable = false, updatable = false)
    var geografica: Geografica? = null,

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "fad_mvp_id", insertable = false, updatable = false)
    var proveedorArticulos: List<ProveedorArticulo>? = null,

    ) : Auditable()
