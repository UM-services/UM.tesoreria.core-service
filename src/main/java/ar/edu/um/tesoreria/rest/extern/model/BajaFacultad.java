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
public class BajaFacultad implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8260580906592802175L;

	private Integer facultadId;
	private BigDecimal personaId;
	private Integer documentoId;
	private Integer lectivoId;
	private Long bajaId;
	private Byte definitiva;
	private Byte certificadoanaliticoparcial;
	private Byte libredeuda;
	private Byte previa;
	private Byte temporaria;
	private String motivo;
	private OffsetDateTime fecha;
	
}
