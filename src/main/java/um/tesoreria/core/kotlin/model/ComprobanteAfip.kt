package um.tesoreria.core.kotlin.model

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
