package um.tesoreria.core.kotlin.model

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.json.JsonMapper
import java.math.BigDecimal
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import um.tesoreria.core.kotlin.model.view.ContratadoPersona
import um.tesoreria.core.util.Jsonifier

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["cursoId", "anho", "mes", "contratoId"])
    ]
)
data class CursoCargoContratado(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var cursoCargoContratadoId: Long? = null,

    var cursoId: Long? = null,
    var anho: Int = 0,
    var mes: Int = 0,
    var contratoId: Long? = null,
    var personaId: BigDecimal? = null,
    var documentoId: Int? = null,
    var cargoTipoId: Int? = null,
    var horasSemanales: BigDecimal = BigDecimal.ZERO,
    var horasTotales: BigDecimal = BigDecimal.ZERO,
    var designacionTipoId: Int? = null,
    var categoriaId: Int? = null,
    var cursoCargoNovedadId: Long? = null,
    var acreditado: Byte = 0,

) : Auditable() {

    fun jsonify(): String {
        return Jsonifier.builder(this).build()
    }

    val periodo: String
        get() = "$anho.$mes"

}
