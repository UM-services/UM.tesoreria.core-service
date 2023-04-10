package ar.edu.um.tesoreria.rest.kotlin.model

import ar.edu.um.tesoreria.rest.model.Auditable
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table
data class Valor(

    @Id @Column(name = "tiv_id")
    var valorId: Int? = null,

    @Column(name = "tiv_concepto")
    var concepto: String? = null,

    @Column(name = "tiv_categoria")
    var categoria: String? = null,

    @Column(name = "tiv_retardo")
    var retardo: Int? = null,

    @Column(name = "tiv_vencimiento")
    var vencimiento: Int? = null,

    @Column(name = "tiv_prv_id")
    var proveedorId: Int? = null,

    @Column(name = "tiv_cuenta")
    var numeroCuenta: BigDecimal? = null,

    ) : Auditable()
