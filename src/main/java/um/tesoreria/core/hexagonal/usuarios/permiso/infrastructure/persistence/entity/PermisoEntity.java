package um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.model.Auditable;

@Getter
@Setter
@Entity
@Table(name = "permiso")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermisoEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long permisoId;

    private String clave;

    private String descripcion;

    private String modulo;

    @Builder.Default
    private String aplicacion = "TESORERIA";

    @Builder.Default
    private Byte activo = 1;
}
