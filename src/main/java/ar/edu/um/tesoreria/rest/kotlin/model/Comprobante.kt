package ar.edu.um.tesoreria.rest.kotlin.model

import ar.edu.um.tesoreria.rest.model.Auditable
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
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

    var tipoAfip: Int? = null,

    var puntoVenta: Int? = null,

    ) : Auditable()
