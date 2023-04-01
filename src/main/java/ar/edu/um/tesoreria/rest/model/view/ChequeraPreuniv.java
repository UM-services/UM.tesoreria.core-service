/**
 * 
 */
package ar.edu.um.tesoreria.rest.model.view;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.MessageFormat;

import ar.edu.um.tesoreria.rest.kotlin.model.Facultad;
import ar.edu.um.tesoreria.rest.kotlin.model.Lectivo;
import ar.edu.um.tesoreria.rest.kotlin.model.TipoChequera;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

import ar.edu.um.tesoreria.rest.kotlin.model.ArancelTipo;
import ar.edu.um.tesoreria.rest.model.Geografica;
import ar.edu.um.tesoreria.rest.kotlin.model.Persona;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(name = "vw_chequera_preuniv")
@Immutable
@NoArgsConstructor
@AllArgsConstructor
public class ChequeraPreuniv implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5081653677665140152L;

	@Id
	private Long chequeraId;

	private Integer facultadId;
	private Integer tipoChequeraId;
	private Long chequeraSerieId;
	private Integer lectivoId;
	private Integer geograficaId;
	private Integer arancelTipoId;
	private BigDecimal personaId;
	private Integer documentoId;
	private String personaKey;

	public String getChequera() {
		return MessageFormat.format("{0}/{1}/{2}", this.facultadId, this.tipoChequeraId, this.chequeraSerieId);
	}

	@OneToOne
	@JoinColumn(name = "facultadId", insertable = false, updatable = false)
	private Facultad facultad;

	@OneToOne
	@JoinColumn(name = "tipoChequeraId", insertable = false, updatable = false)
	private TipoChequera tipoChequera;

	@OneToOne
	@JoinColumn(name = "lectivoId", insertable = false, updatable = false)
	private Lectivo lectivo;

	@OneToOne
	@JoinColumn(name = "geograficaId", insertable = false, updatable = false)
	private Geografica geografica;

	@OneToOne
	@JoinColumn(name = "arancelTipoId", insertable = false, updatable = false)
	private ArancelTipo arancelTipo;

	@OneToOne
	@JoinColumns({
			@JoinColumn(name = "personaId", referencedColumnName = "per_id", insertable = false, updatable = false),
			@JoinColumn(name = "documentoId", referencedColumnName = "per_doc_id", insertable = false, updatable = false) })
	private Persona persona;

}
