/**
 * 
 */
package um.tesoreria.core.model;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.*;
import um.tesoreria.core.kotlin.model.Auditable;
import um.tesoreria.core.kotlin.model.Cuenta;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(name = "proveedores")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Proveedor extends Auditable implements Serializable {
	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 6015067871756572602L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "prv_id")
	private Integer proveedorId;

	@Column(name = "prv_cuit")
	private String cuit = "";

	@Column(name = "fantasia")
	private String nombreFantasia = "";

	@Column(name = "prv_razon")
	private String razonSocial = "";

	@Column(name = "orden")
	private String ordenCheque = "";

	@Column(name = "prv_domicilio")
	private String domicilio = "";

	@Column(name = "prv_telefono")
	private String telefono = "";

	@Column(name = "prv_fax")
	private String fax = "";

	@Column(name = "prv_celular")
	private String celular = "";

	@Column(name = "prv_email")
	private String email = "";
	
	private String emailInterno = "";

	@Column(name = "prv_cuenta")
	private BigDecimal numeroCuenta;

	@Column(name = "prv_habilitado")
	private Byte habilitado = 0;

	private String cbu = "";
	
	@OneToOne(optional = true)
	@JoinColumn(name = "prv_cuenta", insertable = false, updatable = false)
	private Cuenta cuenta;

}
