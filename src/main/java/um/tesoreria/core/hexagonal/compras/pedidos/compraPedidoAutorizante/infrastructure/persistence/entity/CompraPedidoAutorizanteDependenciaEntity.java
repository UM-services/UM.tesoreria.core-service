package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.infrastructure.persistence.entity;

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
 * Tabla puente autorizante ↔ dependencia (D-ENV-3).
 *
 * <p>Antes se escribía con {@code JdbcTemplate}: asignar o quitar una dependencia autorizada no
 * dejaba rastro en la auditoría. Con el mapeo JPA cada alta y cada baja emiten su
 * {@code CHANGE}.</p>
 */
@Entity
@Table(name = "compra_pedido_autorizante_dependencia")
@IdClass(CompraPedidoAutorizanteDependenciaId.class)
@Getter
@Setter
@NoArgsConstructor
public class CompraPedidoAutorizanteDependenciaEntity extends Auditable {

    @Id
    @Column(name = "autorizante_id", nullable = false)
    private Integer autorizanteId;

    @Id
    @Column(name = "dependencia_id", nullable = false)
    private Integer dependenciaId;

    public CompraPedidoAutorizanteDependenciaEntity(Integer autorizanteId, Integer dependenciaId) {
        this.autorizanteId = autorizanteId;
        this.dependenciaId = dependenciaId;
    }
}
