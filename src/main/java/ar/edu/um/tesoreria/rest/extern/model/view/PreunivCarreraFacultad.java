/**
 * 
 */
package ar.edu.um.tesoreria.rest.extern.model.view;

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
public class PreunivCarreraFacultad implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 7185104151191006512L;

	protected Long preinscripcionId;
	protected Integer facultadId;
	protected Integer lectivoId;
	protected Integer geograficaId;
	protected Integer turnoId;
	protected BigDecimal personaId;
	protected Integer documentoId;
	private Integer planId;
	private Integer carreraId;

	public String getUnified() {
		return personaId + "." + documentoId;
	}

}
