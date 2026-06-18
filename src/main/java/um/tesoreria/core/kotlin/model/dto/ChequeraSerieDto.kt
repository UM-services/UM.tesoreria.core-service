package um.tesoreria.core.kotlin.model.dto

import java.time.OffsetDateTime
import com.fasterxml.jackson.annotation.JsonFormat
import um.tesoreria.core.util.Jsonifier

data class ChequeraSerieDto(

    var chequeraSerieId: Long? = null,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
	var fecha: OffsetDateTime? = null,
    var observaciones: String = "",
    var alternativaId: Int = 0,
    var facultad: FacultadDto? = null,
    var tipoChequera: TipoChequeraDto? = null,
    var persona: PersonaDto? = null,
    var mails: DomicilioDto? = null,
    var lectivo: LectivoDto? = null,
    var arancelTipo: ArancelTipoDto? = null,
    var geografica: GeograficaDto? = null,
    var chequeraCuotas: List<ChequeraCuotaDto>? = null

) {
    fun jsonify(): String {
        return Jsonifier.builder(this).build()
    }
}