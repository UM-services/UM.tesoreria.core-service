package um.tesoreria.core.kotlin.model

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

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["cursoId", "anho", "mes", "contratadoId"])
    ]
)
data class CursoCargoContratado(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var cursoCargoContratadoId: Long? = null,

    var cursoId: Long? = null,
    var anho: Int = 0,
    var mes: Int = 0,
    var contratadoId: Long? = null,
    var contratoId: Long? = null,
    var cargoTipoId: Int? = null,
    var horasSemanales: BigDecimal = BigDecimal.ZERO,
    var horasTotales: BigDecimal = BigDecimal.ZERO,
    var designacionTipoId: Int? = null,
    var categoriaId: Int? = null,
    var cursoCargoNovedadId: Long? = null,
    var acreditado: Byte = 0,

    @OneToOne
    @JoinColumn(name = "contratadoId", insertable = false, updatable = false)
    var contratadoPersona: ContratadoPersona? = null

) : Auditable() {

    val periodo: String
        get() = "$anho.$mes"

}
