package um.tesoreria.core.hexagonal.contable.ejercicio.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioConflictoException;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioConflictoException.Codigo;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioNoEncontradoException;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.EjercicioSiguiente;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.in.DeleteEjercicioUseCase;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out.EjercicioHistorialPort;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out.EjercicioRepository;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out.EjercicioUsoPort;

import java.time.Clock;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DeleteEjercicioUseCaseImpl implements DeleteEjercicioUseCase {
    private final EjercicioRepository repository;
    private final EjercicioUsoPort uso;
    private final EjercicioHistorialPort historial;
    private final TransactionOperations transaction;
    private final Clock clock;

    @Override
    public void deleteEjercicio(Integer ejercicioId) {
        ReintentoPorBloqueo.ejecutar(() -> transaction.execute(status -> {
            // Primera lectura de la transacción: bloquea el último y serializa altas y bajas.
            Ejercicio ultimo = repository.findLastWithLock()
                    .orElseThrow(() -> new EjercicioNoEncontradoException(ejercicioId));
            if (!ultimo.getEjercicioId().equals(ejercicioId)) {
                if (!repository.existsByEjercicioId(ejercicioId)) {
                    throw new EjercicioNoEncontradoException(ejercicioId);
                }
                throw new EjercicioConflictoException(Codigo.NO_ES_ULTIMO,
                        "Solo se puede borrar el último ejercicio (" + ultimo.getEjercicioId() + ")");
            }
            if (!EjercicioSiguiente.esFuturo(ultimo, LocalDate.now(clock))) {
                throw new EjercicioConflictoException(Codigo.NO_FUTURO,
                        "Solo se puede borrar un ejercicio que inicia en un año futuro");
            }
            if (uso.hayMovimientos(ultimo.getFechaInicio(), ultimo.getFechaFinal())) {
                throw new EjercicioConflictoException(Codigo.EN_USO,
                        "El ejercicio " + ejercicioId + " tiene movimientos contables o asientos en su período");
            }
            // El delete con flush traduce a EN_USO una FK RESTRICT (tablas de Compras).
            repository.deleteByEjercicioId(ejercicioId);
            historial.registrarBaja(ultimo);
            return null;
        }));
    }
}
