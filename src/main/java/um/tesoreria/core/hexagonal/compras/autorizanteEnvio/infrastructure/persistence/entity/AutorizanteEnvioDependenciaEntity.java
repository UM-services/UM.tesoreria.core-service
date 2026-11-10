package um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.model.Auditable;

/**
 * Tabla puente autorizante de envío ↔ dependencia.
 *
 * <p>Cada alta y cada baja pasan por los listeners de Hibernate ({@link Auditable}), por lo
 * que la administración de dependencias habilitadas queda auditada.</p>
 */
@Entity
@Table(name = "compra_autorizante_envio_dependencia")
@IdClass(AutorizanteEnvioDependenciaId.class)
@Getter
@Setter
@NoArgsConstructor
public class AutorizanteEnvioDependenciaEntity extends Auditable {

    @Id
    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Id
    @Column(name = "dependencia_id", nullable = false)
    private Integer dependenciaId;

    public AutorizanteEnvioDependenciaEntity(Integer usuarioId, Integer dependenciaId) {
        this.usuarioId = usuarioId;
        this.dependenciaId = dependenciaId;
    }
}
