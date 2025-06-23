package um.tesoreria.core.kotlin.model.dto

import com.fasterxml.jackson.annotation.JsonFormat
import um.tesoreria.core.model.dto.ChequeraPagoDto
import um.tesoreria.core.model.dto.MercadoPagoContextDto
import java.math.BigDecimal
import java.time.OffsetDateTime

data class ChequeraCuotaDto(

	var chequeraCuotaId: Long = 0,
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
	var producto: ProductoDto? = null,
	var chequeraPagos: List<ChequeraPagoDto> = emptyList(),
	var mercadoPagoContext: MercadoPagoContextDto? = null

)
