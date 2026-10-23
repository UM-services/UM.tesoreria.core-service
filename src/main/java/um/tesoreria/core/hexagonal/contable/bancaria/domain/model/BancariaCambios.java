package um.tesoreria.core.hexagonal.contable.bancaria.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pedido de alta o de edición. Cada dato editable es un {@link Campo}: ausente conserva (edición) o vale el vacío
 * (alta), null explícito vacía. bancariaId es el que trajo el cuerpo y updated es la precondición opcional de la edición.
 */
@Getter
@Builder
public class BancariaCambios {
    private final Long bancariaId;
    private final LocalDateTime updated;
    @Builder.Default
    private final Campo<String> banco = Campo.ausente();
    @Builder.Default
    private final Campo<String> numero = Campo.ausente();
    @Builder.Default
    private final Campo<String> cbu = Campo.ausente();
    @Builder.Default
    private final Campo<String> titular = Campo.ausente();
    @Builder.Default
    private final Campo<String> cuit = Campo.ausente();
    @Builder.Default
    private final Campo<BigDecimal> numeroCuenta = Campo.ausente();
    @Builder.Default
    private final Campo<Integer> proveedorId = Campo.ausente();
}
