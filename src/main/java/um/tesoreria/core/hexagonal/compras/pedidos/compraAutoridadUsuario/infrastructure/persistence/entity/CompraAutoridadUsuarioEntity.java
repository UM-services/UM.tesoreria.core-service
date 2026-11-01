package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.model.Auditable;

/**
 * Tabla puente usuario ↔ perfil de autoridad.
 *
 * <p>Antes se escribía con {@code JdbcTemplate} (INSERT ... ON DUPLICATE KEY UPDATE / DELETE),
 * así que las asignaciones de permisos de compras no quedaban auditadas. Al mapearla como
 * entidad JPA, cada alta y cada baja emiten su {@code CHANGE} por el listener de Hibernate.</p>
 */
@Entity
@Table(name = "compra_autoridad_usuario",
        uniqueConstraints = @UniqueConstraint(name = "uk_compra_autoridad_usuario",
                columnNames = {"usuario_id", "autoridad_perfil_id"}))
@Getter
@Setter
@NoArgsConstructor
public class CompraAutoridadUsuarioEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "autoridad_usuario_id")
    private Long autoridadUsuarioId;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(name = "autoridad_perfil_id", nullable = false)
    private Long autoridadPerfilId;

    public CompraAutoridadUsuarioEntity(Integer usuarioId, Long autoridadPerfilId) {
        this.usuarioId = usuarioId;
        this.autoridadPerfilId = autoridadPerfilId;
    }
}
