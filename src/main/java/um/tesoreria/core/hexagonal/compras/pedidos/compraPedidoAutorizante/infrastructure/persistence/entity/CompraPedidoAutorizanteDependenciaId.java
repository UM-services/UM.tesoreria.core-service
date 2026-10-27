package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/** Clave compuesta de {@code compra_pedido_autorizante_dependencia}. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraPedidoAutorizanteDependenciaId implements Serializable {

    private Integer autorizanteId;

    private Integer dependenciaId;

}
