/**
 * 
 */
package ar.edu.um.tesoreria.rest.extern.model.view;

import java.io.Serializable;

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
public class InscriptoCursoFacultad implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2815851181961541861L;

	private String uniqueId;
	private Integer lectivoId;
	private Integer facultadId;
	private Integer planId;
	private Integer carreraId;
	private Integer curso;
	private Integer geograficaId;
	private Byte provisoria;
	private Integer cantidad;
	private Integer bajas = 0;
	private Integer egresos = 0;

	public String getCarreraKey() {
		return this.facultadId + "." + this.planId + "." + this.carreraId;
	}

	public String getPlanKey() {
		return this.facultadId + "." + this.planId;
	}

}
