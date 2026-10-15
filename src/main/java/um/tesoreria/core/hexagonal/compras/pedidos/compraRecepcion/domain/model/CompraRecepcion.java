package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Recepción y conformidad (etapa 9): <b>una entrega / remito</b> contra una Orden de Compra
 * ({@code compraFormalizacionId}).
 *
 * <p>El avance es acumulativo: sobre la misma OC puede haber varias entregas (parciales) y el
 * saldo por ítem es derivado (nunca se persiste, D-REC-1). El resultado general se calcula del
 * detalle (D-REC-2) y el número {@code RC-AAAA-NNNNNN} se reserva al confirmar (D-REC-3).</p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraRecepcion {

    private Long compraRecepcionId;
    private Long compraFormalizacionId;
    private String numero;
    private Integer ejercicioId;
    private LocalDateTime fecha;
    private String remito;
    private CompraRecepcionResultado resultado;
    private CompraRecepcionPatrimonio patrimonio;
    private String observacion;
    private Integer usuarioId;
    @Builder.Default
    private List<CompraRecepcionItem> items = new ArrayList<>();
    private LocalDateTime created;
    private LocalDateTime updated;

    /** Resultado general derivado del peor resultado por ítem (D-REC-2). */
    public CompraRecepcionResultado resultadoGeneral() {
        return CompraRecepcionResultado.masGrave(items == null ? List.of()
                : items.stream().map(CompraRecepcionItem::getResultado).toList());
    }

}
