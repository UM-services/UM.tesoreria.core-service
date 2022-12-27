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
@Table
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Geografica extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 6459853826353848541L;

	@Id
	@Column(name = "geo_id")
	private Integer geograficaId;

	@Column(name = "geo_nombre")
	private String nombre = "";

	@Column(name = "geo_sinchequera")
	private Byte sinchequera = 0;

}
