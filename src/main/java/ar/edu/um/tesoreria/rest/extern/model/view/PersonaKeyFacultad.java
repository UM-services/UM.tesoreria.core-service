/**
 * 
 */
package ar.edu.um.tesoreria.rest.extern.model.view;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

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
@Table(name = "vw_persona_key")
@Immutable
@NoArgsConstructor
@AllArgsConstructor
public class PersonaKeyFacultad implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 2892747011251907823L;

	@Id
	private Long uniqueId;

	private BigDecimal personaId;
	private Integer documentoId;
	private String apellido;
	private String nombre;
	private String sexo;
	private Integer profesionId;
	private String mascara;

	@Column(name = "key")
	private String unified;

	@Transient
	private Boolean mark_tesoreria = false;

	public String apellidonombre() {
		return this.apellido + ", " + this.nombre;
	}

}
