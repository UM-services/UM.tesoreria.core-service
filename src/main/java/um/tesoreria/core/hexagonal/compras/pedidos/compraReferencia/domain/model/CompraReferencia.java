package um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Valor de referencia por ejercicio: la unidad sobre la que se expresa el múltiplo de los
 * perfiles de autoridad ({@code compra_autoridad_perfil}). El límite efectivo de un usuario
 * es {@code MAX(multiplico) × importe}.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraReferencia {

    private Integer ejercicioId;
    private BigDecimal importe;
    private LocalDateTime created;
    private LocalDateTime updated;
}
