package um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/** Un asiento cuyo debe difiere del haber; {@code diferencia} es debe menos haber, sin redondeo. */
public record AsientoNoCuadrado(
        OffsetDateTime fechaContable,
        Integer ordenContable,
        List<LineaDiario> lineas,
        BigDecimal totalDebe,
        BigDecimal totalHaber,
        BigDecimal diferencia) {

    /** El asiento como no cuadrado, o vacío si debe y haber son iguales en valor (sin importar la escala). */
    public static Optional<AsientoNoCuadrado> de(AsientoDiario asiento) {
        if (asiento.totalDebe().compareTo(asiento.totalHaber()) == 0) {
            return Optional.empty();
        }
        return Optional.of(new AsientoNoCuadrado(asiento.fechaContable(), asiento.ordenContable(),
                asiento.lineas(), asiento.totalDebe(), asiento.totalHaber(),
                asiento.totalDebe().subtract(asiento.totalHaber())));
    }
}
