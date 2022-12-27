/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;

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
public class Valor extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 9174887012477814805L;

	@Id
	@Column(name = "tiv_id")
	private Integer valorId;

	@Column(name = "tiv_concepto")
	private String concepto;

	@Column(name = "tiv_categoria")
	private String categoria;

	@Column(name = "tiv_retardo")
	private Integer retardo;

	@Column(name = "tiv_vencimiento")
	private Integer vencimiento;

	@Column(name = "tiv_prv_id")
	private Integer proveedorId;

	@Column(name = "tiv_cuenta")
	private BigDecimal cuenta;

}
