package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Alta de una entrega (POST): el número, el ejercicio y el resultado general los resuelve el
 * servicio a partir de la Orden de Compra y del detalle.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraRecepcionRequest {

    private Long compraFormalizacionId;
    private LocalDateTime fecha;
    private String remito;
    /** {@code INTERVENIDO} | {@code NO_CORRESPONDE} | {@code PENDIENTE} (por defecto no corresponde). */
    private String patrimonio;
    private String observacion;
    /** Usuario que recepciona (actor de la entrega). */
    private Integer usuarioId;
    private List<CompraRecepcionItemRequest> items;

}
