package ar.edu.um.tesoreria.rest.kotlin.model.dto

import java.math.BigDecimal

data class PersonaDTO(

	var personaId: BigDecimal? = null,
	var documentoId: Int? = null,
	var apellido: String = "",
	var nombre: String = "",

)
	