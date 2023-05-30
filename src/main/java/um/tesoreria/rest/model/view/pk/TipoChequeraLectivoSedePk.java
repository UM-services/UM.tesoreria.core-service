/**
 * 
 */
package um.tesoreria.rest.model.view.pk;

import java.io.Serializable;

import lombok.Data;

/**
 * @author daniel
 *
 */
@Data
public class TipoChequeraLectivoSedePk implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5069405193118972173L;

	private Integer tipoChequeraId;
	private Integer facultadId;
	private Integer lectivoId;
	private Integer geograficaId;
	
}
