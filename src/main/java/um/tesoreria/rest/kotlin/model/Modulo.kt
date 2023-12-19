package um.tesoreria.rest.kotlin.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table
data class Modulo(

    @Id
    @Column(name = "mod_id")
    var moduloId: Int? = null,

    @Column(name = "mod_nombre")
    var nombre: String = ""

) : Auditable()
