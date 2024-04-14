package um.tesoreria.core.kotlin.model

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime

@Entity
@Table
data class Lectivo(

    @Id @Column(name = "lec_id")
    var lectivoId: Int? = null,

    @Column(name = "lec_nombre")
    var nombre: String = "",

    @Column(name = "lec_inicio")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaInicio: OffsetDateTime? = null,

    @Column(name = "lec_fin")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaFinal: OffsetDateTime? = null

) : Auditable()
