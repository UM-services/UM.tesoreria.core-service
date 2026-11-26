package um.tesoreria.core.hexagonal.contable.cuenta.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.CuentaJerarquia;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.GetCuentasImputablesUseCase;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.out.CuentaRepository;

import java.util.List;

/**
 * Cuentas de grado 5 sin escribir en plancta: el grado sale del número con {@link CuentaJerarquia}
 * (el guardado queda como respaldo para números fuera de la estructura) y el nombre se devuelve sin "\r\n".
 */
@Component
@RequiredArgsConstructor
public class GetCuentasImputablesUseCaseImpl implements GetCuentasImputablesUseCase {

    private static final int GRADO_IMPUTABLE = 5;

    private final CuentaRepository repository;

    @Override
    public List<Cuenta> getCuentasImputables() {
        return repository.findAllConNumeroPositivo().stream()
                .map(GetCuentasImputablesUseCaseImpl::normalizar)
                .filter(cuenta -> Integer.valueOf(GRADO_IMPUTABLE).equals(cuenta.getGrado()))
                .toList();
    }

    private static Cuenta normalizar(Cuenta cuenta) {
        if (cuenta.getNombre() != null) {
            cuenta.setNombre(cuenta.getNombre().replace("\r\n", ""));
        }
        final CuentaJerarquia.Grados grados;
        try {
            grados = CuentaJerarquia.grados(cuenta.getNumeroCuenta());
        } catch (IllegalArgumentException ex) {
            return cuenta;
        }
        cuenta.setGrado(grados.grado());
        cuenta.setGrado1(grados.grado1());
        cuenta.setGrado2(grados.grado2());
        cuenta.setGrado3(grados.grado3());
        cuenta.setGrado4(grados.grado4());
        return cuenta;
    }
}
