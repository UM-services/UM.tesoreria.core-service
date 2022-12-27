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
public class PlanFacultad implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5765639085835136953L;

	private Integer facultadId;
	private Integer planId;
	private Long uniqueId;
	private String nombre;
	private OffsetDateTime fecha;
	private Byte publicar;
	private Integer semanas;

	public String getPlanKey() {
		return this.facultadId + "." + this.planId;
	}

}
