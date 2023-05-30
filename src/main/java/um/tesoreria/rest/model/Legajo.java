/**
 * 
 */
package um.tesoreria.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonFormat;

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
@Table(name = "aluleg", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "ale_fac_id", "ale_per_id", "ale_doc_id" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Legajo extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3950450930864967230L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ale_id")
	private Long legajoId;

	@Column(name = "ale_per_id")
	private BigDecimal personaId;

	@Column(name = "ale_doc_id")
	private Integer documentoId;

	@Column(name = "ale_fac_id")
	private Integer facultadId;

	@Column(name = "ale_leg_id")
	private Long numeroLegajo = 0L;

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
	private Byte tieneCarrera = 0;

	@Column(name = "ale_geo_id")
	private Integer geograficaId;

	@Column(name = "ale_contrasenia")
	private String contrasenha;

	@Column(name = "intercambio")
	private Byte intercambio = 0;

}
