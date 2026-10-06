package um.tesoreria.core.hexagonal.ubicacionArticulo.application.usecases;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception.UbicacionArticuloValidationException;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.in.SaveUbicacionArticuloUseCase;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.out.UbicacionArticuloRepository;

/**
 * Una asignación por transacción. El reintento ante un choque vive en {@code UbicacionArticuloService}
 * (otro bean): una transacción que falló queda marcada para rollback y no se puede reutilizar.
 */
@Component
@RequiredArgsConstructor
public class SaveUbicacionArticuloUseCaseImpl implements SaveUbicacionArticuloUseCase {
    /** {@code ubicacion_articulo.cuenta_contable decimal(11,0)} en dev. */
    static final int CUENTA_DIGITOS = 11;

    private final UbicacionArticuloRepository repository;
    @Override
    @Transactional
    public UbicacionArticulo save(UbicacionArticulo ubicacionArticulo) {
        if (ubicacionArticulo.getUbicacionId() == null) {
            throw new UbicacionArticuloValidationException("ubicacionId", "ubicacionId es obligatorio.");
        }
        if (ubicacionArticulo.getArticuloId() == null) {
            throw new UbicacionArticuloValidationException("articuloId", "articuloId es obligatorio.");
        }
        if (!cuentaEntraEnColumna(ubicacionArticulo.getNumeroCuenta())) {
            throw new UbicacionArticuloValidationException("numeroCuenta",
                    "numeroCuenta debe ser un número entero de hasta " + CUENTA_DIGITOS + " dígitos.");
        }
        return repository.save(ubicacionArticulo);
    }

    /**
     * Nula entra (el vínculo puede no tener cuenta). Un valor fuera de la columna sería un 500 en la base, y uno con
     * exponente enorme (1e999999999) haría que el driver arme un texto gigante.
     */
    static boolean cuentaEntraEnColumna(BigDecimal cuenta) {
        if (cuenta == null) {
            return true;
        }
        var normalizada = cuenta.stripTrailingZeros();
        long digitosEnteros = (long) normalizada.precision() - normalizada.scale();
        return normalizada.scale() <= 0 && digitosEnteros <= CUENTA_DIGITOS;
    }
}
