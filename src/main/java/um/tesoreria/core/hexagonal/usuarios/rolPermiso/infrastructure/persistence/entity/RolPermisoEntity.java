package um.tesoreria.core.hexagonal.usuarios.rolPermiso.infrastructure.persistence.entity;

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
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.persistence.entity.RolEntity;
import um.tesoreria.core.model.Auditable;

@Getter
@Setter
@Entity
@Table(name = "rol_permiso", uniqueConstraints = {
        @UniqueConstraint(name = "uq_rol_permiso", columnNames = { "rolId", "permisoId" })
})
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolPermisoEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rolPermisoId;

    private Long rolId;
    private Long permisoId;

    // Excepción cross-slice autorizada: asociaciones de solo lectura a los catálogos
    // usuarios.rol y usuarios.permiso (mismo patrón que usuarioChequeraGeografica).
    @OneToOne(optional = true)
    @JoinColumn(name = "rolId", insertable = false, updatable = false)
    private RolEntity rol;

    @OneToOne(optional = true)
    @JoinColumn(name = "permisoId", insertable = false, updatable = false)
    private PermisoEntity permiso;
}
