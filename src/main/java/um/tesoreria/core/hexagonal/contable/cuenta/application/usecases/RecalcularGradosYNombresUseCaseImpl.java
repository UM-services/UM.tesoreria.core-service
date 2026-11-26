package um.tesoreria.core.hexagonal.contable.cuenta.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.contable.cuenta.application.exception.CuentaConflictoException;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.RecalculoGrados;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.in.RecalcularGradosYNombresUseCase;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.out.CuentaRepository;

@Component
@RequiredArgsConstructor
public class RecalcularGradosYNombresUseCaseImpl implements RecalcularGradosYNombresUseCase {

    private final CuentaRepository repository;
    private final TransactionOperations transaction;

    @Override
    public RecalculoGrados recalcularGradosYNombres() {
        try {
            return transaction.execute(status -> {
                int grados = repository.recalcularGrados();
                return new RecalculoGrados(grados, repository.limpiarNombres());
            });
        } catch (PessimisticLockingFailureException ex) {
            // Un alta, edición o baja concurrente bloquea las mismas filas: es un conflicto, no un error del servidor.
            throw new CuentaConflictoException("Conflicto concurrente al recalcular los grados del plan de cuentas", ex);
        }
    }
}
