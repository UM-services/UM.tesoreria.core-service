package ar.edu.um.tesoreria.rest.model.kotlin.dto

import java.time.OffsetDateTime
import com.fasterxml.jackson.annotation.JsonFormat

data class ChequeraSerieDTO(

	var chequeraSerieId: Long? = null,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	var fecha: OffsetDateTime? = null,
	var observaciones: String = "",
	var alternativaId: Int = 0,
	var facultad: FacultadDTO? = null,
	var tipoChequera: TipoChequeraDTO? = null,
	var persona: PersonaDTO? = null,
	var lectivo: LectivoDTO? = null,
	var arancelTipo: ArancelTipoDTO? = null,
	var geografica: GeograficaDTO? = null,
	var chequeraCuotas: List<ChequeraCuotaDTO>? = null

)