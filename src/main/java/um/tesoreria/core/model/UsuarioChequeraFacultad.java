/**
 * 
 */
package um.tesoreria.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.*;
import um.tesoreria.core.kotlin.model.Auditable;
import um.tesoreria.core.kotlin.model.Facultad;
import um.tesoreria.core.kotlin.model.Usuario;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(uniqueConstraints = {
           @UniqueConstraint(name = "uk_usuario_facultad", columnNames = {"userId", "facultadId"})
       })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioChequeraFacultad extends Auditable {
	/**
	 * 
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long usuarioChequeraFacultadId = null;

	private Long userId = null;
	private Integer facultadId = null;

	@OneToOne(optional = true)
	@JoinColumn(name = "userId", insertable = false, updatable = false)
	private Usuario usuario = null;

	@OneToOne(optional = true)
	@JoinColumn(name = "facultadId", insertable = false, updatable = false)
	private Facultad facultad = null;

} 