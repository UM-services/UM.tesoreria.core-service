package um.tesoreria.rest.kotlin.model.internal

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.OffsetDateTime
import java.math.BigDecimal

data class AsientoInternal(

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	var fechaContable: OffsetDateTime? = null,

	var ordenContable: Int,
	var debe: BigDecimal = BigDecimal.ZERO,
	var haber: BigDecimal = BigDecimal.ZERO

)