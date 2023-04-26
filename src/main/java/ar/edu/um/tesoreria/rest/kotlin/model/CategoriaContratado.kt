package ar.edu.um.tesoreria.rest.kotlin.model

import ar.edu.um.tesoreria.rest.model.Auditable
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table
data class CategoriaContratado(

    @Id
    var categoriaId: Int? = null,

    var nombre: String = "",
    var importe: BigDecimal = BigDecimal.ZERO,

    ) : Auditable()
