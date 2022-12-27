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
public class Producto extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3720041375561227094L;

	@Id
	@Column(name = "pro_id")
	private Integer productoId;
	
	@Column(name = "pro_nombre")
	private String nombre = "";

}
