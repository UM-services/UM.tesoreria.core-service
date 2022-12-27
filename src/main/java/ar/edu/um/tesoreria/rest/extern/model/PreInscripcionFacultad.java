/**
 * 
 */
package ar.edu.um.tesoreria.rest.extern.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
public class PreInscripcionFacultad implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5119519803283924663L;

	private Long preinscripcionId;
	private Integer facultadId;
	private Integer lectivoId;
	private Integer geograficaId;
	private Integer turnoId;
	private BigDecimal personaId;
	private Integer documentoId;
	private OffsetDateTime fecha;
	private String chequera;
	private String observaciones;
	private String barras;

	public String getPersonaKey() {
		return this.personaId + "." + this.documentoId;
	}

}
