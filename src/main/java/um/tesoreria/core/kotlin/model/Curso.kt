package um.tesoreria.core.kotlin.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table
data class Curso(

    @Id
    @Column(name = "cur_id")
    var cursoId: Int? = null,

    @Column(name = "cur_nombre")
    var nombre: String? = null,

    var claseChequeraId: Int? = null,

): Auditable()
