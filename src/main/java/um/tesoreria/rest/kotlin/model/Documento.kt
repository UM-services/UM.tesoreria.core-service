package um.tesoreria.rest.kotlin.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table
data class Documento(

    @Id
    @Column(name = "doc_id")
    var documentoId: Int? = null,

    @Column(name = "doc_nombre")
    var nombre: String = "",

    ) : Auditable()
