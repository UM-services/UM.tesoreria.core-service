package ar.edu.um.tesoreria.rest.kotlin.model

import ar.edu.um.tesoreria.rest.model.Auditable
import ar.edu.um.tesoreria.rest.model.Cuenta
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table
data class Bancaria(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cub_id")
    var bancariaId: Long? = null,

    @Column(name = "cub_banco")
    var banco: String = "",

    @Column(name = "cub_numero")
    var numero: String = "",

    @Column(name = "cub_cbu")
    var cbu: String = "",

    @Column(name = "cub_titular")
    var titular: String = "",

    @Column(name = "cub_cuit")
    var cuit: String = "",

    @Column(name = "cub_pla_cuenta")
    var numeroCuenta: BigDecimal? = null,

    @Column(name = "cub_prv_id")
    var proveedorId: Int? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "cub_pla_cuenta", insertable = false, updatable = false)
    var cuenta: Cuenta? = null,

    ) : Auditable()
