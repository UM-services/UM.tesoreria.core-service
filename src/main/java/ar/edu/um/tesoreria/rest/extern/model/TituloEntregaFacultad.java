/**
 * 
 */
package ar.edu.um.tesoreria.rest.extern.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.Data;

/**
 * @author daniel
 *
 */
@Data
public class TituloEntregaFacultad implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -216361084143866462L;

	private BigDecimal personaId;
	private Integer documentoId;
	private Integer facultadId;
	private Integer planId;
	private Integer carreraId;
	private Long tituloentregaId;
	private OffsetDateTime inicio;
	private OffsetDateTime entrega;
	private Long folio;
	private Integer libro;
	private String ministerio;
	private String formula;
	private String titulogrado;
	private String titulotrabajo;
	private BigDecimal personaIdasesor;
	private Integer documentoIdasesor;
	private OffsetDateTime fechaultima;
	private String materiaIdultima;
	private String libroultima;
	private Long folioultima;
	private Integer geograficaId;

}
