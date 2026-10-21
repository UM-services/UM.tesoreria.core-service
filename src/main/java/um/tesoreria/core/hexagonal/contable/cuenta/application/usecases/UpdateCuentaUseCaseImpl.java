package um.tesoreria.core.hexagonal.contable.cuenta.application.usecases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.contable.cuenta.application.exception.CuentaConflictoException;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.CuentaJerarquia;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.out.CuentaHistorialPort;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.UpdateCuentaUseCase;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.out.CuentaRepository;
import java.math.BigDecimal;
import java.util.Optional;
@Component
@RequiredArgsConstructor
public class UpdateCuentaUseCaseImpl implements UpdateCuentaUseCase {
    private final CuentaRepository repository;
    private final CuentaHistorialPort historial;
    private final TransactionOperations transaction;
    @Override
    public Optional<Cuenta> updateCuenta(BigDecimal numeroCuenta, Cuenta cuenta) {
        return transaction.execute(status -> repository.findWithLockByNumeroCuenta(numeroCuenta).map(anterior -> {
            if (cuenta.getNombre() != null && cuenta.getNombre().isBlank()) {
                throw new CuentaConflictoException("El nombre de la cuenta es obligatorio");
            }
            final CuentaJerarquia.Grados grados;
            try {
                grados = CuentaJerarquia.grados(numeroCuenta);
            } catch (IllegalArgumentException ex) {
                throw new CuentaConflictoException(ex.getMessage());
            }
            Byte integradora = cuenta.getIntegradora() == null ? anterior.getIntegradora() : cuenta.getIntegradora();
            var rango = CuentaJerarquia.rangoDescendientes(numeroCuenta);
            if (repository.existenHijas(rango.desde(), rango.hasta())) {
                integradora = (byte) 1;
            }
            Integer sede = cuenta.getGeograficaId() == null ? anterior.getGeograficaId() : cuenta.getGeograficaId();
            Cuenta nueva = Cuenta.builder().numeroCuenta(anterior.getNumeroCuenta())
                    .cuentaContableId(anterior.getCuentaContableId())
                    .nombre(cuenta.getNombre() == null ? anterior.getNombre() : cuenta.getNombre())
                    .geograficaId(Integer.valueOf(0).equals(sede) ? null : sede)
                    .integradora(integradora).grado(grados.grado())
                    .grado1(grados.grado1()).grado2(grados.grado2()).grado3(grados.grado3()).grado4(grados.grado4())
                    .fechaBloqueo(cuenta.getFechaBloqueo() == null ? anterior.getFechaBloqueo() : cuenta.getFechaBloqueo())
                    .visible(cuenta.getVisible() == null ? anterior.getVisible() : cuenta.getVisible()).build();
            Cuenta guardada = repository.update(numeroCuenta, nueva)
                    .orElseThrow(() -> new CuentaConflictoException("La cuenta ya no existe"));
            historial.registrarEdicion(anterior, guardada);
            return guardada;
        }));
    }
}
