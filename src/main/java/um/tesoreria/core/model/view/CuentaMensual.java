/**
 * 
 */
package um.tesoreria.core.model.view;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

import um.tesoreria.core.model.view.pk.CuentaMensualPk;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(name = "vw_cuentamensual")
@Immutable
@IdClass(value = CuentaMensualPk.class)
@NoArgsConstructor
@AllArgsConstructor
public class CuentaMensual implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4346343724642180577L;

	@Id
	private BigDecimal cuenta;

	@Id
	private Integer anho;

	@Id
	private Integer mes;

	private String nombre;
	private BigDecimal debe;
	private BigDecimal haber;

}
