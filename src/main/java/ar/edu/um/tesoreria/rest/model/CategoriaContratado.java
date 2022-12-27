/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;

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
public class CategoriaContratado extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -7627833487679606596L;

	@Id
	private Integer categoriaId;

	private String nombre = "";
	private BigDecimal importe = BigDecimal.ZERO;

}
