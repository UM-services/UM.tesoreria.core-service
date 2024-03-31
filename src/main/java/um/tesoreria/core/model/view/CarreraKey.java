/**
 * 
 */
package um.tesoreria.core.model.view;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import um.tesoreria.core.kotlin.model.Auditable;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(name = "vw_carrera_key")
@Immutable
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class CarreraKey extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 3230864082986337431L;

	@Id
	private String unified;
	
	@Column(name = "clave")
	private Long uniqueId;

	@Column(name = "car_fac_id")
	private Integer facultadId;
	
	@Column(name = "car_pla_id")
	private Integer planId;
	
	@Column(name = "car_id")
	private Integer carreraId;

	@Column(name = "car_nombre")
	private String nombre;
	
	@Column(name = "car_iniciales")
	private String iniciales;
	
	@Column(name = "car_titulo")
	private String titulo;
	
	@Column(name = "car_chequnica")
	private Byte chequeraunica;
	
}
