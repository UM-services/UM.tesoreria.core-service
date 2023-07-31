/**
 * 
 */
package um.tesoreria.rest.model.view;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import org.hibernate.annotations.Immutable;

import com.fasterxml.jackson.annotation.JsonFormat;

import um.tesoreria.rest.model.Auditable;
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
@Immutable
@Table(name = "vw_chequera_clase", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "chs_fac_id", "chs_tch_id", "chs_id" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ChequeraClase extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -2590458450461741507L;

	@Id
	@Column(name = "clave")
	private Long chequeraId;

	@Column(name = "clasechequera_id")
	private Integer claseChequeraId;

	@Column(name = "chs_fac_id")
	private Integer facultadId;

	@Column(name = "chs_tch_id")
	private Integer tipoChequeraId;

	@Column(name = "chs_id")
	private Long chequeraSerieId;

	@Column(name = "chs_per_id")
	private BigDecimal personaId;

	@Column(name = "chs_doc_id")
	private Integer documentoId;

	@Column(name = "chs_lec_id")
	private Integer lectivoId;

	@Column(name = "chs_art_id")
	private Integer arancelTipoId;

	@Column(name = "chs_cur_id")
	private Integer cursoId;

	@Column(name = "chs_asentado")
	private Integer asentado;

	@Column(name = "chs_geo_id")
	private Integer geograficaId;

	@Column(name = "chs_fecha")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fecha;

	@Column(name = "chs_cuotasp")
	private Integer cuotasPagadas;

	@Column(name = "chs_observ")
	private String observaciones;

	@Column(name = "chs_alt_id")
	private Integer alternativaId;

	@Column(name = "chs_algopagado")
	private Byte algoPagado;

	@Column(name = "chs_tim_id")
	private Integer tipoImpresionId;

	@Column(name = "flag_paypertic")
	private Byte flagPayperTic;

	private String usuarioId;
	private Byte enviado;

}
