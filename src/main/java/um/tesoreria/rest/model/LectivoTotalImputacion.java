/**
 * 
 */
package um.tesoreria.rest.model;

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
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = { "facultadId", "lectivoId", "tipoChequeraId", "productoId" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class LectivoTotalImputacion extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1978491899027884455L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long lectivoTotalImputacionId;

	private Integer facultadId;
	private Integer lectivoId;
	private Integer tipoChequeraId;
	private Integer productoId;
	private BigDecimal cuenta;

}
