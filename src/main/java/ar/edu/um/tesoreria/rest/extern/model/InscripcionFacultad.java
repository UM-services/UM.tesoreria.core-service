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
public class InscripcionFacultad implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2430193945316706645L;

	private Integer facultadId;
	private BigDecimal personaId;
	private Integer documentoId;
	private Integer lectivoId;
	private Long inscripcionId;
	private OffsetDateTime fecha;
	private String chequera = "";
	private String matricula = "";
	private Long factura = 0L;
	private Integer curso = 0;
	private Integer planId;
	private Integer carreraId;
	private Integer geograficaId;
	private Byte asentado = 0;
	private Byte provisoria = 0;
	private Integer cohorte = 0;
	private Byte remota = 0;
	private Byte imprimir = 0;
	private Integer edad = 0;
	private String observaciones = "";
	private Integer offsetpago = 0;
	private Integer libre = 0;
	private Integer divisionId;
	private Byte debematricula = 0;

	public String getPersonaKey() {
		return this.personaId + "." + this.documentoId;
	}

	public String getCarreraKey() {
		return this.facultadId + "." + this.planId + "." + this.carreraId;
	}

	public String getPlanKey() {
		return this.facultadId + "." + this.planId;
	}

	public String getLegajoKey() {
		return this.facultadId + "." + this.personaId + "." + this.documentoId;
	}

}
