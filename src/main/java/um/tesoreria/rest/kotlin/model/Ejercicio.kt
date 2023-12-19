package um.tesoreria.rest.kotlin.model

import um.tesoreria.rest.util.Tool
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime

@Entity
@Table(name = "ejercicios")
data class Ejercicio(

    @Id
    @Column(name = "eje_id")
    var ejercicioId: Int? = null,

    @Column(name = "eje_nombre")
    var nombre: String? = null,

    @Column(name = "eje_fechainicio")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaInicio: OffsetDateTime? = Tool.dateAbsoluteArgentina(),

    @Column(name = "eje_fechafin")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    var fechaFinal: OffsetDateTime? = Tool.dateAbsoluteArgentina(),

    @Column(name = "eje_bloqueado")
    var bloqueado: Byte = 0,

    var ordenContableResultado: Int? = null,
    var ordenContableBienesUso: Int? = null

) : Auditable()
