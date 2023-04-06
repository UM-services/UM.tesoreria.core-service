package ar.edu.um.tesoreria.rest.kotlin.model

import ar.edu.um.tesoreria.rest.model.Auditable
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint


@Entity
@Table(
    name = "movprov_detallevalores",
    uniqueConstraints = [UniqueConstraint(columnNames = ["opv_orp_id", "opv_orden"])]
)
data class ProveedorValor(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "opv_id")
    var proveedorValorId: Long? = null,

    @Column(name = "opv_orp_id")
    var proveedorMovimientoId: Long? = null,

    @Column(name = "opv_orden")
    var orden: Int? = null,

    @Column(name = "opv_val_id")
    var valorMovimientoId: Long? = null,

    ) : Auditable()
