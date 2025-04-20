package um.tesoreria.core.kotlin.model.dto

import java.time.OffsetDateTime
import com.fasterxml.jackson.annotation.JsonFormat

data class LectivoDto(

	var nombre: String = "",
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	var fechaInicio: OffsetDateTime? = null,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	var fechaFinal: OffsetDateTime? = null

)
