/**
 * 
 */
package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.*;
import um.tesoreria.core.hexagonal.dependencias.facultad.infrastructure.persistence.entity.FacultadEntity;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.persistence.entity.UsuarioEntity;
import um.tesoreria.core.model.Auditable;

/**
 * @author daniel
 *
 */
@Data
@Entity
@Table(name = "usuario_chequera_facultad", uniqueConstraints = {
           @UniqueConstraint(name = "uk_usuario_facultad", columnNames = {"userId", "facultadId"})
       })
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioChequeraFacultadEntity extends Auditable {
	/**
	 * 
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long usuarioChequeraFacultadId;

	private Long userId;
	private Integer facultadId;

	@OneToOne(optional = true)
	@JoinColumn(name = "userId", insertable = false, updatable = false)
	private UsuarioEntity usuario;

	@OneToOne(optional = true)
	@JoinColumn(name = "facultadId", insertable = false, updatable = false)
	private FacultadEntity facultad;

} 