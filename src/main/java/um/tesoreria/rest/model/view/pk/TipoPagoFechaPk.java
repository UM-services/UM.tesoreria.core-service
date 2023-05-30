/**
 * 
 */
package um.tesoreria.rest.model.view.pk;

import java.io.Serializable;
import java.time.OffsetDateTime;

import lombok.Data;

/**
 * @author daniel
 *
 */
@Data
public class TipoPagoFechaPk implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 537516388702539508L;

	private Integer tipoPagoId;
	private OffsetDateTime fechaAcreditacion;

}
