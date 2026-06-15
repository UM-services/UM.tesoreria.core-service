package um.tesoreria.core.kotlin.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import um.tesoreria.core.model.Auditable

@Entity
data class FacultadGrupo(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var facultadGrupoId: Int? = null,

    var facultadId: Int? = null,
    var grupo: Int? = null

) : Auditable()
