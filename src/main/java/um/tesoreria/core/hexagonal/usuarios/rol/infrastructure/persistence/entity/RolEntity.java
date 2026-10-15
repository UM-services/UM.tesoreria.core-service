package um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.persistence.entity;

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
@Table(name = "rol")
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rolId;

    private String nombre;

    private String descripcion;

    @Builder.Default
    private String aplicacion = "TESORERIA";

    @Builder.Default
    private Byte activo = 1;
}
