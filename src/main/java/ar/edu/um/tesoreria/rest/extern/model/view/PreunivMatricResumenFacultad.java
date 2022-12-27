/**
 * 
 */
package ar.edu.um.tesoreria.rest.extern.model.view;

import java.io.Serializable;

import lombok.Data;

/**
 * @author daniel
 *
 */
@Data
public class PreunivMatricResumenFacultad implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 4146581711823419158L;

	private Integer facultadId;
	private Integer lectivoId;
	private Integer planId;
	private Integer carreraId;
	private Integer geograficaId;
	private Integer turnoId;
	private Integer cantidad;

	public String getKey() {
		return this.facultadId + "." + this.lectivoId + "." + this.planId + "." + this.carreraId + "."
				+ this.geograficaId + "." + this.turnoId;
	}

}
