package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

/** Fila de {@code ingreso_asiento}: asocia una fecha y un tipo de pago con su orden contable. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsientoIngresoRegistro {

    private Long id;
    private OffsetDateTime fechaContable;
    private Integer tipoPagoId;
    private Integer ordenContable;

}
