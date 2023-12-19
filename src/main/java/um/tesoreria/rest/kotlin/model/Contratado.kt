package um.tesoreria.rest.kotlin.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table
data class Contratado(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var contratadoId: Long? = null,

    var personaClave: Long? = null,

    @OneToOne
    @JoinColumn(name = "personaClave", insertable = false, updatable = false)
    var persona: Persona? = null

) : Auditable()
