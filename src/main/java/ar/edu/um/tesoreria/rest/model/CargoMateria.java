/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "cargomateria")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class CargoMateria extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 4197006793534538460L;

	@Id
	@Column(name = "cam_id")
	private Integer cargomateriaId;

	@Column(name = "cam_nombre")
	private String nombre = "";

	@Column(name = "cam_precedencia")
	private Integer precedencia = 0;

}
