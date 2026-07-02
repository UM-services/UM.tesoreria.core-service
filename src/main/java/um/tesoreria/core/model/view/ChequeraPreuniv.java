/**
 * 
 */
package um.tesoreria.core.model.view;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.MessageFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.infrastructure.persistence.entity.ArancelTipoEntity;
import um.tesoreria.core.hexagonal.facultad.infrastructure.persistence.entity.FacultadEntity;
import um.tesoreria.core.hexagonal.geografica.infrastructure.persistence.entity.GeograficaEntity;
import um.tesoreria.core.hexagonal.lectivo.infrastructure.persistence.entity.LectivoEntity;
import um.tesoreria.core.hexagonal.persona.infrastructure.persistence.entity.PersonaEntity;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.persistence.entity.TipoChequeraEntity;

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
	@Serial
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
	private FacultadEntity facultad;

	@OneToOne
	@JoinColumn(name = "tipoChequeraId", insertable = false, updatable = false)
	private TipoChequeraEntity tipoChequera;

	@OneToOne
	@JoinColumn(name = "lectivoId", insertable = false, updatable = false)
	private LectivoEntity lectivo;

	@OneToOne
	@JoinColumn(name = "geograficaId", insertable = false, updatable = false)
	private GeograficaEntity geografica;

	@OneToOne
	@JoinColumn(name = "arancelTipoId", insertable = false, updatable = false)
	private ArancelTipoEntity arancelTipo;

	@OneToOne
	@JoinColumns({
			@JoinColumn(name = "personaId", referencedColumnName = "per_id", insertable = false, updatable = false),
			@JoinColumn(name = "documentoId", referencedColumnName = "per_doc_id", insertable = false, updatable = false) })
	private PersonaEntity persona;

}
