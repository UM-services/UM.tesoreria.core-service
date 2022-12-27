/**
 * 
 */
package ar.edu.um.tesoreria.rest.extern.model;

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
public class InscripcionDetalleFacultad implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -463504594518308390L;

	private BigDecimal personaId;
	private Integer documentoId;
	private Integer lectivoId;
	private Integer facultadId;
	private Integer planId;
	private String materiaId;
	private Long inscripciondetalleId;
	private Integer cursoId = 0;
	private Integer periodoId = 0;
	private Integer divisionId;
	private Byte recursa = 0;
	private Byte imprimir = 0;
	private Byte moroso = 0;
	private Byte libre = 0;
	private Byte condicional = 0;

}
