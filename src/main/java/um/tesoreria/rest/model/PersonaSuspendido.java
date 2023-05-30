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
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "personaId", "documentoId" }) })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class PersonaSuspendido extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6683275243411569907L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long personaSuspendidoId;

	private BigDecimal personaId;
	private Integer documentoId;
	private Integer facultadId;
	private Integer geograficaId;
	private Byte notificado;

	public String personaKey() {
		return this.personaId + "." + this.documentoId;
	}

}
