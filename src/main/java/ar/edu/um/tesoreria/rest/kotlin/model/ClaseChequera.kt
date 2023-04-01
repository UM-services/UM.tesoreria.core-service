package ar.edu.um.tesoreria.rest.kotlin.model

import ar.edu.um.tesoreria.rest.model.Auditable
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table
data class ClaseChequera(

    @Id
    @Column(name = "cch_id")
    var claseChequeraId: Int? = null,

    @Column(name = "cch_nombre")
    val nombre: String? = null,

    ) : Auditable()
