/**
 * 
 */
package um.tesoreria.core.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import um.tesoreria.core.kotlin.model.Auditable;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table
@EqualsAndHashCode(callSuper=false)
public class Postal extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5185845112186468471L;

	@Id
	@Column(name = "pos_codigo")
	private Integer codigopostal;

	@Column(name = "pos_distrito")
	private String distrito = "";

	@Column(name = "pos_localidad")
	private String localidad = "";

	@Column(name = "pos_provincia")
	private String provincia = "";

}
