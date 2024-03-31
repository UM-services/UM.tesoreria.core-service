/**
 * 
 */
package um.tesoreria.core.model;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table
public class InfoLdap implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8237890775828015918L;

	@Id
	private BigDecimal personaId;
	
	private String dominio;
	private String emailInstitucional;
	private String apellido;
	private String nombre;
	
}
