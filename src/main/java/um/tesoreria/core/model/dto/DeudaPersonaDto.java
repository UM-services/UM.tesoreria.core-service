/**
 * 
 */
package um.tesoreria.core.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daniel
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeudaPersonaDto implements Serializable {

	private BigDecimal personaId;
	private Integer documentoId;
	private Integer cuotas;
	private BigDecimal deuda;
	private List<DeudaChequeraDto> deudas;
	private List<VencimientoDto> vencimientos;

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
