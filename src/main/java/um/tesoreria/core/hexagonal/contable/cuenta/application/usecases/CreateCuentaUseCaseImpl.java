package um.tesoreria.core.hexagonal.contable.cuenta.application.usecases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.contable.cuenta.application.exception.CuentaConflictoException;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.CuentaJerarquia;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.out.CuentaHistorialPort;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.CreateCuentaUseCase;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.out.CuentaRepository;
@Component
@RequiredArgsConstructor
public class CreateCuentaUseCaseImpl implements CreateCuentaUseCase {
    private final CuentaRepository repository;
    private final CuentaHistorialPort historial;
    private final TransactionOperations transaction;
    @Override
    public Cuenta createCuenta(Cuenta cuenta) {
        final CuentaJerarquia.Grados grados;
        try {
            grados = CuentaJerarquia.grados(cuenta.getNumeroCuenta());
        } catch (IllegalArgumentException ex) {
            throw new CuentaConflictoException(ex.getMessage());
        }
        try {
            return transaction.execute(status -> {
                List<Cuenta> ancestros = new ArrayList<>();
                // Primera sentencia SQL: bloqueo de la raíz. Todas las altas siguen el mismo orden.
                for (BigDecimal numero : CuentaJerarquia.ancestros(cuenta.getNumeroCuenta())) {
                    ancestros.add(repository.findWithLockByNumeroCuenta(numero)
                            .orElseThrow(() -> new CuentaConflictoException("Falta la cuenta ancestro " + numero)));
                }
                if (repository.findWithLockByNumeroCuenta(cuenta.getNumeroCuenta()).isPresent()) {
                    throw new CuentaConflictoException("La cuenta " + cuenta.getNumeroCuenta() + " ya existe");
                }
                for (Cuenta ancestro : ancestros) {
                    BigDecimal numero = ancestro.getNumeroCuenta();
                    if (repository.existenMovimientos(numero, numero)) {
                        throw new CuentaConflictoException("La cuenta ancestro " + numero + " contiene registraciones");
                    }
                }
                cuenta.setGrado(grados.grado());
                cuenta.setGrado1(grados.grado1());
                cuenta.setGrado2(grados.grado2());
                cuenta.setGrado3(grados.grado3());
                cuenta.setGrado4(grados.grado4());
                if (Integer.valueOf(0).equals(cuenta.getGeograficaId())) {
                    cuenta.setGeograficaId(null);
                }
                // VB6 da de alta con visible=1; sin el dato la cuenta no debe quedar oculta.
                if (cuenta.getVisible() == null) {
                    cuenta.setVisible((byte) 1);
                }
                Cuenta creada = repository.create(cuenta);
                historial.registrarAlta(creada);
                for (Cuenta ancestro : ancestros) {
                    if (!Byte.valueOf((byte) 1).equals(ancestro.getIntegradora())) {
                        Cuenta nueva = Cuenta.builder()
                                .numeroCuenta(ancestro.getNumeroCuenta()).nombre(ancestro.getNombre())
                                .integradora((byte) 1).grado(ancestro.getGrado())
                                .grado1(ancestro.getGrado1()).grado2(ancestro.getGrado2())
                                .grado3(ancestro.getGrado3()).grado4(ancestro.getGrado4())
                                .geograficaId(ancestro.getGeograficaId()).fechaBloqueo(ancestro.getFechaBloqueo())
                                .visible(ancestro.getVisible()).cuentaContableId(ancestro.getCuentaContableId()).build();
                        Cuenta guardada = repository.update(ancestro.getNumeroCuenta(), nueva)
                                .orElseThrow(() -> new CuentaConflictoException("Falta la cuenta ancestro " + ancestro.getNumeroCuenta()));
                        historial.registrarEdicion(ancestro, guardada);
                    }
                }
                return creada;
            });
        } catch (DataIntegrityViolationException ex) {
            throw new CuentaConflictoException("No se pudo crear la cuenta " + cuenta.getNumeroCuenta() + ": número duplicado o registros asociados", ex);
        } catch (PessimisticLockingFailureException ex) {
            // Dos altas de una raíz inexistente pueden competir por el hueco antes del INSERT.
            throw new CuentaConflictoException("Conflicto concurrente al crear la cuenta " + cuenta.getNumeroCuenta(), ex);
        }
    }
}
