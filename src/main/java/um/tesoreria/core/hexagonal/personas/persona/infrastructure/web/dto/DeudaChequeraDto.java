/**
 * 
 */
package um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daniel
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeudaChequeraDto implements Serializable {
	/**
	* 
	*/
	@Serial
	private static final long serialVersionUID = 8285161457827104236L;

	private BigDecimal personaId;
	private Integer documentoId;
	private Integer facultadId;
	private String facultad;
	private Integer tipochequeraId;
	private String tipochequera;
	private Long chequeraserieId;
	private Integer lectivoId;
	private String lectivo;
	private Integer alternativaId;
	private BigDecimal total;
	private BigDecimal deuda;
	private Integer cuotas;
	private Long chequeraId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
	private OffsetDateTime vencimiento1;

	private BigDecimal importe1;

	/**
	 * Alias legacy: el cliente VB6 de Gestión lee las claves camelCase. Se emiten
	 * además de las grafías originales, sin renombrarlas, para no romper a los
	 * consumidores actuales.
	 */
	@JsonAnyGetter
	public java.util.Map<String, Object> legacyIds() {
		java.util.Map<String, Object> ids = new java.util.LinkedHashMap<>();
		ids.put("tipoChequeraId", tipochequeraId);
		ids.put("chequeraSerieId", chequeraserieId);
		return ids;
	}

    public String jsonify() {
        try {
            return JsonMapper
                    .builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "jsonify error -> " + e.getMessage();
        }
    }

}
