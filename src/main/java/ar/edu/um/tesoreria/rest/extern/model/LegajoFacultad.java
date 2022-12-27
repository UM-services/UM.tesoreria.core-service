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

	private BigDecimal personaId;
	private Integer documentoId;
	private Integer facultadId;
	private Long legajoId;
	private Integer geograficaId;
	private Long numeroLegajo;
	private Integer lectivoId;
	private OffsetDateTime fecha;
	private Integer planId;
	private Integer carreraId;
	private String contrasenha;
	private Byte intercambio;
}
