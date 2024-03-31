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
public class FacultadLectivoSedePk implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4009660432512275898L;

	private Integer facultadId;
	private Integer lectivoId;
	private Integer geograficaId;

}
