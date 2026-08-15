/**
 *
 */
package um.tesoreria.core.hexagonal.personas.legajo.infrastructure.persistence.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.OneToOne;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;
import um.tesoreria.core.kotlin.model.Carrera;
import um.tesoreria.core.model.Auditable;
import um.tesoreria.core.util.Jsonifyable;

/**
 * @author daniel
 *
 */
@Getter
@Setter
@Entity
@Table(
		name = "aluleg",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = { "ale_fac_id", "ale_per_id", "ale_doc_id" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LegajoEntity extends Auditable implements Jsonifyable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ale_id")
	private Long legajoId = null;

	@Column(name = "ale_per_id")
	private BigDecimal personaId = null;

	@Column(name = "ale_doc_id")
	private Integer documentoId = null;

	@Column(name = "ale_fac_id")
	private Integer facultadId = null;

	@Column(name = "ale_leg_id")
	private Long numeroLegajo = 0L;

	@Column(name = "ale_fecha")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
	private OffsetDateTime fecha = null;

	@Column(name = "ale_lec_id")
	private Integer lectivoId = null;

	@Column(name = "ale_pla_id")
	private Integer planId = null;

	@Column(name = "ale_car_id")
	private Integer carreraId = null;

	@Column(name = "ale_carrera")
	private Byte tieneCarrera = 0;

	@Column(name = "ale_geo_id")
	private Integer geograficaId = null;

	@Column(name = "ale_contrasenia")
	private String contrasenha = null;

	private Byte intercambio = 0;

	@OneToOne(optional = true)
	@JoinColumns({
			@JoinColumn(name = "ale_fac_id", referencedColumnName = "car_fac_id", insertable = false, updatable = false),
			@JoinColumn(name = "ale_pla_id", referencedColumnName = "car_pla_id", insertable = false, updatable = false),
			@JoinColumn(name = "ale_car_id", referencedColumnName = "car_id", insertable = false, updatable = false) })
	private Carrera carrera = null;

}
