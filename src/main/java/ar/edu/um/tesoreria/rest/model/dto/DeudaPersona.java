/**
 * 
 */
package ar.edu.um.tesoreria.rest.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

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
public class DeudaPersona implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 309300403078520665L;

	private BigDecimal personaId;
	private Integer documentoId;
	private Integer cuotas;
	private BigDecimal deuda;
	private List<DeudaChequera> deudas;

}
