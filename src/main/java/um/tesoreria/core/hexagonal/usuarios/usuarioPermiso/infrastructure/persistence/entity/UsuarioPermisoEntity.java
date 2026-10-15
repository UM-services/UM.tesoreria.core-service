package um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.infrastructure.persistence.entity;

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
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.persistence.entity.PermisoEntity;
import um.tesoreria.core.model.Auditable;

@Getter
@Setter
@Entity
@Table(name = "usuario_permiso", uniqueConstraints = {
        @UniqueConstraint(name = "uq_usuario_permiso", columnNames = { "userId", "permisoId" })
})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioPermisoEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usuarioPermisoId;

    private Long userId;
    private Long permisoId;
    private Byte otorgado;

    // Excepción cross-slice autorizada: asociación de solo lectura al catálogo usuarios.permiso.
    @OneToOne(optional = true)
    @JoinColumn(name = "permisoId", insertable = false, updatable = false)
    private PermisoEntity permiso;
}
