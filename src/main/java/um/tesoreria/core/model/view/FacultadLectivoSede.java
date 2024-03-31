/**
 * 
 */
package um.tesoreria.core.model.view;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import org.hibernate.annotations.Immutable;

import um.tesoreria.core.model.view.pk.FacultadLectivoSedePk;
import lombok.Data;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Immutable
@Table(name = "vw_facultad_lectivo_sede")
@IdClass(value = FacultadLectivoSedePk.class)
public class FacultadLectivoSede implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 547679473103087678L;

	@Id
	private Integer lectivoId;

	@Id
	private Integer geograficaId;
	
	@Id
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

}
