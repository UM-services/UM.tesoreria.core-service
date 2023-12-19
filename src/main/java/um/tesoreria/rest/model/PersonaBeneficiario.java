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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import um.tesoreria.rest.kotlin.model.Auditable;
import um.tesoreria.rest.kotlin.model.Persona;

/**
 * @author daniel
 *
 */
@Data
@Table
@Entity
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class PersonaBeneficiario extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 4529768518427197870L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long personaBeneficiarioId;
	
	private Long personaUniqueId;
	private BigDecimal documento;
	private String apellidoNombre = "";
	private String cuit;
	private String cbu;

	@OneToOne
	@JoinColumn(name = "personaUniqueId", insertable = false, updatable = false)
	private Persona persona;

}
