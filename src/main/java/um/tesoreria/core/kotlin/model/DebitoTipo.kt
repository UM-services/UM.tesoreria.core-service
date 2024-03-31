package um.tesoreria.core.kotlin.model

import jakarta.persistence.*

@Entity
@Table
data class DebitoTipo(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var debitoTipoId: Int? = null,

    var nombre: String = "",

    ) : Auditable()
