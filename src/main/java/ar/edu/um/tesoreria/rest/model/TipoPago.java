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
@Table(name = "tipopago")
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
public class TipoPago extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -4739977530340335996L;

	@Id
	@Column(name = "tpa_id")
	private Integer tipoPagoId;
	
	@Column(name = "tpa_nombre")
	private String nombre = "";

}
