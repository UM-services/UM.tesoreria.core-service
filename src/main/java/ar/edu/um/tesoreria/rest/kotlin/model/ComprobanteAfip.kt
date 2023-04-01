package ar.edu.um.tesoreria.rest.kotlin.model

import ar.edu.um.tesoreria.rest.model.Auditable
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table
data class ComprobanteAfip(

    @Id
    var comprobanteAfipId: Int? = null,
    var nombre: String = "",
    var label: String = "",

) : Auditable()
