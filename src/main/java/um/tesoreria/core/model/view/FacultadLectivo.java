/**
 * 
 */
package um.tesoreria.core.model.view;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

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
@Table(name = "vw_facultad_lectivo")
@Immutable
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class FacultadLectivo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8329039450038751621L;

	@Id
	private String unified;

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

	private Integer lectivoId;
	
}
