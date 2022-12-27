/**
 * 
 */
package ar.edu.um.tesoreria.rest.model.view.pk;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * @author daniel
 *
 */
@Data
public class CuentaMensualPk implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4778340046017469848L;
	
	private BigDecimal cuenta;
	private Integer anho;
	private Integer mes;	
}
