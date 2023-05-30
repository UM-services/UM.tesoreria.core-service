/**
 * 
 */
package um.tesoreria.rest.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;

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
public class DeudaChequera implements Serializable {
	/**
	* 
	*/
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

}
