package um.tesoreria.core.kotlin.model

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import um.tesoreria.core.model.Auditable
import um.tesoreria.core.util.Jsonifier
import java.time.OffsetDateTime

@Entity
@Table
data class Lectivo(

    @Id @Column(name = "lec_id")
    var lectivoId: Int? = null,

    @Column(name = "lec_nombre")
    var nombre: String = "",

    @Column(name = "lec_inicio")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    var fechaInicio: OffsetDateTime? = null,

    @Column(name = "lec_fin")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    var fechaFinal: OffsetDateTime? = null

) : Auditable() {
    fun jsonify(): String {
        return Jsonifier.builder(this).build()
    }
}
