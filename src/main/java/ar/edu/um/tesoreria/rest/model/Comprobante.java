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
@Table(name = "tiposcomprob")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Comprobante extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = -67735353263838005L;

	@Id
	@Column(name = "tco_id")
	private Integer comprobanteId;

	@Column(name = "tco_descripcion")
	private String descripcion;

	@Column(name = "tco_modulo")
	private Integer tipoTransaccionId;

	@Column(name = "tco_opago")
	private Byte ordenPago = 0;

	@Column(name = "tco_aplicapend")
	private Byte aplicaPendiente = 0;

	@Column(name = "tco_ctacte")
	private Byte cuentaCorriente = 0;

	@Column(name = "tco_debita")
	private Byte debita = 0;

	@Column(name = "tco_diasvigencia")
	private Integer diasVigencia = 0;

}
