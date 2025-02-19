/**
 * 
 */
package um.tesoreria.core.model.view;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

import com.fasterxml.jackson.annotation.JsonFormat;

import um.tesoreria.core.kotlin.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(name = "vw_chequera_key")
@Immutable
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ChequeraKey extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6401541993132700141L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long chequeraId;

	private Integer facultadId;
	private Integer tipoChequeraId;
	private Long chequeraSerieId;
	private BigDecimal personaId;
	private Integer documentoId;
	private Integer lectivoId;
	private Integer arancelTipoId;
	private Integer cursoId;
	private Integer asentado;
	private Integer geograficaId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fecha;

	private Integer cuotasPagadas;
	private String observaciones;
	private Integer alternativaId;
	private Byte algoPagado;
	private Integer tipoImpresionId;
	private Byte flagPayperTic;
	private String usuarioId;
	private Byte enviado;
	private String chequeraKey;
	
	@OneToOne
	@JoinColumn(name = "facultadId", insertable = false, updatable = false)
	private Facultad facultad;

	@OneToOne
	@JoinColumn(name = "tipoChequeraId", insertable = false, updatable = false)
	private TipoChequera tipoChequera;

	@OneToOne
	@JoinColumns({
			@JoinColumn(name = "personaId", referencedColumnName = "per_id", insertable = false, updatable = false),
			@JoinColumn(name = "documentoId", referencedColumnName = "per_doc_id", insertable = false, updatable = false) })
	private Persona persona;

	@OneToOne
	@JoinColumn(name = "lectivoId", insertable = false, updatable = false)
	private Lectivo lectivo;

	@OneToOne
	@JoinColumn(name = "arancelTipoId", insertable = false, updatable = false)
	private ArancelTipo arancelTipo;

	@OneToOne
	@JoinColumn(name = "geograficaId", insertable = false, updatable = false)
	private Geografica geografica;

}
