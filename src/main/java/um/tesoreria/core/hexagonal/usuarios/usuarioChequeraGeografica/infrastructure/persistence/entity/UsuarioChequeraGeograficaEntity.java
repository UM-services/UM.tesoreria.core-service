package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.*;
import um.tesoreria.core.hexagonal.dependencias.geografica.infrastructure.persistence.entity.GeograficaEntity;
import um.tesoreria.core.model.Auditable;

@Getter
@Setter
@Entity
@Table(name = "usuario_chequera_geografica", uniqueConstraints = {
		@UniqueConstraint(name = "uk_usuario_geografica", columnNames = { "userId", "geograficaId" })
		})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioChequeraGeograficaEntity extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long usuarioChequeraGeograficaId;

	private Long userId;
	private Integer geograficaId;

	@OneToOne(optional = true)
	@JoinColumn(name = "geograficaId", insertable = false, updatable = false)
	private GeograficaEntity geografica;

}
