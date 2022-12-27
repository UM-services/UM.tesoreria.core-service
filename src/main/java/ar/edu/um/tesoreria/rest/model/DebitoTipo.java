/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class DebitoTipo extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6300591706957385798L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer debitoTipoId;

	private String nombre;
}
