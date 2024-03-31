/**
 * 
 */
package um.tesoreria.core.model.view;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(name = "vw_contratado_persona")
@Immutable
@NoArgsConstructor
@AllArgsConstructor
public class ContratadoPersona implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -3947729039514048661L;

	@Id
	private Long contratadoId;

	private BigDecimal personaId;
	private Integer documentoId;
	private String apellido;
	private String nombre ;

}
