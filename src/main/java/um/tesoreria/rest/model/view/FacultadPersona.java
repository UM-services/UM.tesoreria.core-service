/**
 * 
 */
package um.tesoreria.rest.model.view;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

import um.tesoreria.rest.model.Auditable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import um.tesoreria.rest.model.Auditable;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(name = "vw_facultad_persona")
@Immutable
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
public class FacultadPersona extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3279153298895079602L;

	@Id
	private Long chequeraId;

	@Column(name = "fac_id")
	private Integer facultadId;

	@Column(name = "fac_nombre")
	private String nombre = "";

	@Column(name = "fac_codigo_empresa")
	private String codigoempresa = "";

	@Column(name = "fac_server")
	private String server = "";

	@Column(name = "fac_db_adm")
	private String dbadm = "";

	@Column(name = "fac_dsn")
	private String dsn = "";

	@Column(name = "fac_pla_cuenta")
	private BigDecimal cuentacontable = new BigDecimal(0);

	@Column(name = "api_server")
	private String apiserver = "";

	@Column(name = "api_port")
	private Long apiport = 0L;

	private BigDecimal personaId;
	private Integer documentoId;
	private Integer lectivoId;

}
