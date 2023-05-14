package ar.edu.um.tesoreria.rest.kotlin.model

import ar.edu.um.tesoreria.rest.model.Auditable
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table
data class Ubicacion(

    @Id
    var ubicacionId: Int? = null,

    var nombre: String = "",
    var dependenciaId: Int? = null,

    @OneToOne(optional = true)
    @JoinColumn(name = "dependenciaId", insertable = false, updatable = false)
    var dependencia: Dependencia? = null

) : Auditable()
