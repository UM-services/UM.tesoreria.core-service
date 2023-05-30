/**
 * 
 */
package um.tesoreria.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;

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
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "anho", "mes" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class CoeficienteInflacion extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1246557931727210977L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long coeficienteinflacionId;

	private Integer anho;
	private Integer mes;
	private BigDecimal coeficiente;

}
