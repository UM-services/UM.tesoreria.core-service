package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.*;
import um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.persistence.entity.ClaseChequeraEntity;
import um.tesoreria.core.model.Auditable;

@Getter
@Setter
@Entity
@Table(name = "usuario_chequera_clase_chequera", uniqueConstraints = {
		@UniqueConstraint(name = "uk_usuario_clase_chequera", columnNames = { "userId", "claseChequeraId" })
		})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioChequeraClaseChequeraEntity extends Auditable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long usuarioChequeraClaseChequeraId;

	private Long userId;
	private Integer claseChequeraId;

	@OneToOne(optional = true)
	@JoinColumn(name = "claseChequeraId", insertable = false, updatable = false)
	private ClaseChequeraEntity claseChequera;

}
