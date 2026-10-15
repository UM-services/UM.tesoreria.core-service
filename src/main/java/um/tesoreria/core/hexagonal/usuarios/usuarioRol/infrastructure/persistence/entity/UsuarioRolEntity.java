package um.tesoreria.core.hexagonal.usuarios.usuarioRol.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.persistence.entity.RolEntity;
import um.tesoreria.core.model.Auditable;

@Getter
@Setter
@Entity
@Table(name = "usuario_rol", uniqueConstraints = {
        @UniqueConstraint(name = "uq_usuario_rol", columnNames = { "userId", "rolId" })
})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioRolEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usuarioRolId;

    private Long userId;
    private Long rolId;

    // Excepción cross-slice autorizada: asociación de solo lectura al catálogo usuarios.rol.
    @OneToOne(optional = true)
    @JoinColumn(name = "rolId", insertable = false, updatable = false)
    private RolEntity rol;
}
