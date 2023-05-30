package um.tesoreria.rest.kotlin.model

import um.tesoreria.rest.model.Auditable
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "tipopago")
data class TipoPago(

    @Id
    @Column(name = "tpa_id")
    var tipoPagoId: Int? = null,

    @Column(name = "tpa_nombre")
    var nombre: String = "",

    ) : Auditable()
