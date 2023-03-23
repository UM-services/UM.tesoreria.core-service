/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;

import ar.edu.um.tesoreria.rest.kotlin.model.Persona;
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

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Contratado extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -4661969963293420067L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long contratadoId;

	private Long personaClave;

	@OneToOne
	@JoinColumn(name = "personaClave", insertable = false, updatable = false)
	private Persona persona;

}
