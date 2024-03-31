/**
 * 
 */
package um.tesoreria.core.model.view;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import um.tesoreria.core.kotlin.model.Auditable;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(name = "vw_legajo_key")
@Immutable
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class LegajoKey extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -761921612439147704L;

	@Id
	private String unified;
	
	@Column(name = "ale_id")
	private Long legajoId;

	@Column(name = "ale_per_id")
	private BigDecimal personaId;

	@Column(name = "ale_doc_id")
	private Integer documentoId;

	@Column(name = "ale_fac_id")
	private Integer facultadId;

	@Column(name = "ale_leg_id")
	private Long numerolegajo;

	@Column(name = "ale_fecha")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fecha;

	@Column(name = "ale_lec_id")
	private Integer lectivoId;

	@Column(name = "ale_pla_id")
	private Integer planId;

	@Column(name = "ale_car_id")
	private Integer carreraId;

	@Column(name = "ale_carrera")
	private Byte tienecarrera;

	@Column(name = "ale_geo_id")
	private Integer geograficaId;

	@Column(name = "ale_contrasenia")
	private String contrasenha;

	private Byte intercambio;

}
