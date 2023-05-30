package um.tesoreria.rest.kotlin.model.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.OffsetDateTime

data class ChequeraCuotaDTO(

	var cuotaId: Int = 0,
	var mes: Int = 0,
	var anho: Int = 0,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	var vencimiento1: OffsetDateTime? = null,
	var importe1: BigDecimal = BigDecimal.ZERO,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	var vencimiento2: OffsetDateTime? = null,
	var importe2: BigDecimal = BigDecimal.ZERO,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	var vencimiento3: OffsetDateTime? = null,
	var importe3: BigDecimal = BigDecimal.ZERO,
	var pagado: Byte = 0,
	var producto: ProductoDTO? = null

)
