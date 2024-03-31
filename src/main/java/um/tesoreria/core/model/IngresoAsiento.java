/**
 * 
 */
package um.tesoreria.core.model;

import java.io.Serializable;
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
import um.tesoreria.core.kotlin.model.Auditable;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "ina_fecha", "ina_tpa_id" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class IngresoAsiento extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 6599826744249714203L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ina_id")
	private Long ingresoAsientoId;

	@Column(name = "ina_tpa_id")
	private Integer tipoPagoId;

	@Column(name = "ina_fecha")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
	private OffsetDateTime fechaContable;
	
	@Column(name = "ina_nrocomp")
	private Integer ordenContable;

}
