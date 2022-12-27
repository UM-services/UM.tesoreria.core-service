/**
 * 
 */
package ar.edu.um.tesoreria.rest.extern.model;

import java.io.Serializable;
import java.time.OffsetDateTime;

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
public class PreTurnoFacultad implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 6030476707501270581L;

	protected Long uniqueId;
	protected Integer facultadId;
	protected Integer lectivoId;
	protected Integer geograficaId;
	protected Integer turnoId;
	protected String nombre;
	protected OffsetDateTime inicio;
	protected OffsetDateTime fin;

	public String getKey() {
		return this.facultadId + "." + this.lectivoId + "." + this.geograficaId + "." + this.turnoId;
	}

}
