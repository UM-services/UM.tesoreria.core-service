/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

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
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "lec_fac_id", "lec_lec_id", "lec_tch_id", "lec_pro_id",
		"lec_alt_id", "lec_cuo_id" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class LectivoCuota extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4065131077451107247L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lectivocuota_id")
	private Long lectivoCuotaId;

	@Column(name = "lec_fac_id")
	private Integer facultadId;

	@Column(name = "lec_lec_id")
	private Integer lectivoId;

	@Column(name = "lec_tch_id")
	private Integer tipoChequeraId;

	@Column(name = "lec_pro_id")
	private Integer productoId;

	@Column(name = "lec_alt_id")
	private Integer alternativaId;

	@Column(name = "lec_cuo_id")
	private Integer cuotaId;

	@Column(name = "lec_mes")
	private Integer mes;

	@Column(name = "lec_anio")
	private Integer anho;

	@Column(name = "lec_1er_vencimiento")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime vencimiento1;

	@Column(name = "lec_1er_importe")
	private BigDecimal importe1;

	@Column(name = "lec_2do_vencimiento")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime vencimiento2;

	@Column(name = "lec_2do_importe")
	private BigDecimal importe2;

	@Column(name = "lec_3er_vencimiento")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime vencimiento3;

	@Column(name = "lec_3er_importe")
	private BigDecimal importe3;

	@Column(name = "lec_tra_id")
	private Integer tramoId;

}
