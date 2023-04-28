/**
 * 
 */
package ar.edu.um.tesoreria.rest.extern.model;

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
public class CarreraFacultad implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6104456910693552875L;

	private Integer facultadId;
	private Integer planId;
	private Integer carreraId;
	private Long uniqueId;
	private String nombre;
	private String iniciales;
	private String titulo;
	private Byte trabajoFinal;
	private String resolucion;
	private Byte chequeraUnica;
	private Integer bloqueId;
	private Integer obligatorias;
	private Integer optativas;
	private Byte vigente;

	public String getCarreraKey() {
		return this.facultadId + "." + this.planId + "." + this.carreraId;
	}

}
