/**
 * 
 */
package um.tesoreria.rest.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table
@EqualsAndHashCode(callSuper=false)
public class NotificacionExamen extends Auditable implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 775156706723950428L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "notificacion_examen_id")
	private Long notificacionexamenId;

	private Integer facultadId;
	private Integer geograficaId;

	@Column(name = "e_mail")
	private String email;

}
