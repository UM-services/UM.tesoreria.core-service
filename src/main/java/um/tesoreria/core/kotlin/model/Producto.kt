package um.tesoreria.core.kotlin.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table
data class Producto(

    @Id
    @Column(name = "pro_id")
    var productoId: Int? = null,

    @Column(name = "pro_nombre")
    var nombre: String = ""

) : Auditable()
