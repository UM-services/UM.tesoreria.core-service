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
public class LegajoFacultad implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4563166998420069077L;

	private BigDecimal personaId = null;
	private Integer documentoId = null;
	private Integer facultadId = null;
	private Long legajoId = null;
	private Integer geograficaId = null;
	private Long numeroLegajo = null;
	private Integer lectivoId = null;
	private OffsetDateTime fecha = null;
	private Integer planId = null;
	private Integer carreraId = null;
	private String contrasenha = "";
	private Byte intercambio = 0;
	
}
