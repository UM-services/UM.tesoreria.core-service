package um.tesoreria.rest.kotlin.model

import um.tesoreria.rest.model.Auditable
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
    var nombre: String? = null,

    var preuniversitario: Byte = 0,
    var grado: Byte = 0,
    var posgrado: Byte = 0,
    var curso: Byte = 0,
    var secundario: Byte = 0,

    ) : Auditable()
