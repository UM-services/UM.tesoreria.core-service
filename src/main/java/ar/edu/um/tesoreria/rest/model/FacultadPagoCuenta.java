/**
 * 
 */
package ar.edu.um.tesoreria.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

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
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "facultadId", "tipoPagoId" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class FacultadPagoCuenta extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 145988456099718397L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long facultadPagoCuentaId;

	private Integer facultadId;
	private Integer tipoPagoId;
	private BigDecimal cuenta;

}
