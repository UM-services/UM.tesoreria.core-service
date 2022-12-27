/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

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
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = { "aranceltipoId", "productoId" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ArancelPorcentaje extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 2658422436465673615L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long arancelporcentajeId;

	private Integer aranceltipoId;
	private Integer productoId;
	private BigDecimal porcentaje = new BigDecimal(0);

}
