package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Cierre manual de la recepción de una OC con saldo abierto (motivo obligatorio). */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CerrarCompraRecepcionRequest {

    private String motivo;
    private Integer usuarioId;

}
