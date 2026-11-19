package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Una línea del asiento de ingreso, ya consolidada por cuenta. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsientoIngresoLinea {

    private BigDecimal numeroCuenta;
    private String nombreCuenta;
    private Byte debita;
    private BigDecimal importe;

}
