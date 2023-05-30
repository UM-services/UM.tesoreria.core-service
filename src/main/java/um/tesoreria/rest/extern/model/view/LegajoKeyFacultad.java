/**
 * 
 */
package um.tesoreria.rest.extern.model.view;

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
public class LegajoKeyFacultad implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -6753881640078996038L;

	private Long legajoId;
	private BigDecimal personaId;
	private Integer documentoId;
	private Integer facultadId;
	private Integer geograficaId;
	private Long numerolegajo;
	private Integer lectivoId;
	private OffsetDateTime fecha;
	private Integer planId;
	private Integer carreraId;
	private String contrasenha;
	private Byte intercambio;
	private String personakey;

	public String getLegajoKey() {
		return this.facultadId + "." + this.personakey;
	}

}
