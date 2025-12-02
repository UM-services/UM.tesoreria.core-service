package um.tesoreria.core.kotlin.model.dto

import java.math.BigDecimal

data class PersonaDto(

    var personaId: BigDecimal? = null,
    var documentoId: Int? = null,
    var apellido: String? = "",
    var nombre: String? = ""

)
	