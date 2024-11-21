/**
 * 
 */
package um.tesoreria.core.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

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
public class DeudaPersona implements Serializable {

	private BigDecimal personaId;
	private Integer documentoId;
	private Integer cuotas;
	private BigDecimal deuda;
	private List<DeudaChequera> deudas;
	private List<Vencimiento> vencimientos;

}
