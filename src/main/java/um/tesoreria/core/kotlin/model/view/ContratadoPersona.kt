package um.tesoreria.core.kotlin.model.view

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Immutable
import java.math.BigDecimal

@Entity
@Table(name = "vw_contratado_persona")
@Immutable
data class ContratadoPersona(

    @Id
    var uniqueId: String? = null,

    var personaId: BigDecimal? = null,
    var documentoId: Int? = null,
    var apellido: String? = null,
    var nombre: String? = null,
    var cuit: String? = null

)
