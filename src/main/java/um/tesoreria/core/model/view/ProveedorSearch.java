/**
 * 
 */
package um.tesoreria.core.model.view;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "vw_proveedor_search")
@Immutable
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorSearch extends Auditable implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 1134357311262824693L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "prv_id")
	private Integer proveedorId;

	@Column(name = "prv_cuit")
	private String cuit;

	@Column(name = "fantasia")
	private String nombreFantasia;

	@Column(name = "prv_razon")
	private String razonSocial;

	@Column(name = "orden")
	private String ordenCheque;

	@Column(name = "prv_domicilio")
	private String domicilio;

	@Column(name = "prv_telefono")
	private String telefono;

	@Column(name = "prv_fax")
	private String fax;

	@Column(name = "prv_celular")
	private String celular;

	@Column(name = "prv_email")
	private String email;

	@Column(name = "prv_cuenta")
	private BigDecimal cuenta;

	@Column(name = "prv_habilitado")
	private Byte habilitado;

	private String search;

}
