/**
 * 
 */
package um.tesoreria.core.model.view.pk;

import java.io.Serializable;

import lombok.Data;

/**
 * @author daniel
 *
 */
@Data
public class IngresoPeriodoPk implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -389154497735296351L;

	private Integer facultadId;
	private Integer geograficaId;
	private Integer tipopagoId;
	private Integer mes;
	private Integer anho;

}
