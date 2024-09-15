package um.tesoreria.core.kotlin.model

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["pla_fac_id", "pla_id"])])
data class Plan(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clave")
    var uniqueId: Long? = null,

    @Column(name = "pla_fac_id")
    var facultadId: Int,

    @Column(name = "pla_id")
    var planId: Int,

    @Column(name = "pla_nombre")
    var nombre: String = "",

    @Column(name = "pla_fecha")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fecha: OffsetDateTime,

    @Column(name = "pla_publicar")
    var publicar: Byte = 0
) : Auditable() {

    fun getPlanKey(): String {
        return "$facultadId.$planId"
    }

}
