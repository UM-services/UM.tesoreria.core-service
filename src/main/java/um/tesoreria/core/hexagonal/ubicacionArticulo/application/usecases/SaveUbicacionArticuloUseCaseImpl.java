package um.tesoreria.core.hexagonal.ubicacionArticulo.application.usecases;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        if (ubicacionArticulo.getUbicacionId() == null || ubicacionArticulo.getUbicacionId() < 1) {
            throw new UbicacionArticuloValidationException("ubicacionId", "ubicacionId es obligatorio y mayor que cero.");
        }
        var articuloId = ubicacionArticulo.getArticuloId();
        // articulo_id es int: fuera de rango MySQL lo rechazaría con un error sin traducir (500)
        if (articuloId == null || articuloId < 1 || articuloId > Integer.MAX_VALUE) {
            throw new UbicacionArticuloValidationException("articuloId", "articuloId es obligatorio y debe estar entre 1 y " + Integer.MAX_VALUE + ".");
        }
        if (!cuentaEntraEnColumna(ubicacionArticulo.getNumeroCuenta())) {
            throw new UbicacionArticuloValidationException("numeroCuenta",
                    "numeroCuenta debe ser un número entero de hasta " + CUENTA_DIGITOS + " dígitos.");
        }
        if (ubicacionArticulo.getNumeroCuenta() != null) {
            // La escala de la columna: el driver escribe el valor con su escala original (ver cuentaEntraEnColumna)
            ubicacionArticulo.setNumeroCuenta(ubicacionArticulo.getNumeroCuenta().setScale(0, RoundingMode.UNNECESSARY));
        }
        return repository.save(ubicacionArticulo);
    }

    /**
     * Nula entra (el vínculo puede no tener cuenta). Un valor fuera de la columna sería un 500 en la base, y uno con
     * exponente enorme (1e999999999) haría que el driver arme un texto gigante.
     */
    static boolean cuentaEntraEnColumna(BigDecimal cuenta) {
        if (cuenta == null || cuenta.signum() == 0) {
            return true;
        }
        // Dígitos enteros sobre el valor tal cual: con exponentes enormes stripTrailingZeros desbordaría la escala
        if ((long) cuenta.precision() - cuenta.scale() > CUENTA_DIGITOS) {
            return false;
        }
        return cuenta.stripTrailingZeros().scale() <= 0;
    }
}
