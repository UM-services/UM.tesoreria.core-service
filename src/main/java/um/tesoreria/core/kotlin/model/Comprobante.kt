package um.tesoreria.core.kotlin.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "tiposcomprob")
data class Comprobante(

    @Id
    @Column(name = "tco_id")
    var comprobanteId: Int? = null,

    @Column(name = "tco_descripcion")
    var descripcion: String = "",

    @Column(name = "tco_modulo")
    var tipoTransaccionId: Int? = null,

    @Column(name = "tco_opago")
    var ordenPago: Byte = 0,

    @Column(name = "tco_aplicapend")
    var aplicaPendiente: Byte = 0,

    @Column(name = "tco_ctacte")
    var cuentaCorriente: Byte = 0,

    @Column(name = "tco_debita")
    var debita: Byte = 0,

    @Column(name = "tco_diasvigencia")
    var diasVigencia: Long = 0,

    var facturacionElectronica: Byte = 0,
    var comprobanteAfipId: Int? = null,
    var puntoVenta: Int? = null,
    var letraComprobante: String? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "comprobanteAfipId", insertable = false, updatable = false)
    var comprobanteAfip: ComprobanteAfip? = null

) : Auditable()
