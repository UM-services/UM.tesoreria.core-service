/**
 * 
 */
package um.tesoreria.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

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
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = { "let_fac_id", "let_lec_id", "let_tch_id", "let_pro_id" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class LectivoTotal extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -649709995943583891L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lectivototal_id")
	private Long lectivoTotalId;

	@Column(name = "let_fac_id")
	private Integer facultadId;

	@Column(name = "let_lec_id")
	private Integer lectivoId;

	@Column(name = "let_tch_id")
	private Integer tipoChequeraId;

	@Column(name = "let_pro_id")
	private Integer productoId;

	@Column(name = "let_total")
	private BigDecimal total = BigDecimal.ZERO;

}
