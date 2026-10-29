package um.tesoreria.core.hexagonal.contable.ejercicio.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioConflictoException;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioConflictoException.Codigo;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioNoEncontradoException;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.in.UpdateEjercicioUseCase;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out.EjercicioHistorialPort;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out.EjercicioRepository;

import java.util.Objects;

/** Solo se editan bloqueado y los órdenes contables; null conserva lo guardado. */
@Component
@RequiredArgsConstructor
public class UpdateEjercicioUseCaseImpl implements UpdateEjercicioUseCase {
    private final EjercicioRepository repository;
    private final EjercicioHistorialPort historial;
    private final TransactionOperations transaction;

    @Override
    public Ejercicio updateEjercicio(Integer ejercicioId, Ejercicio pedido) {
        Ejercicio cambios = EjercicioValidaciones.sinSentinelas(pedido);
        EjercicioValidaciones.validar(cambios);
        return ReintentoPorBloqueo.ejecutar(() -> transaction.execute(status -> {
            // Primera lectura de la fila en la transacción: con bloqueo, así dos PUT se serializan.
            Ejercicio anterior = repository.findWithLockByEjercicioId(ejercicioId)
                    .orElseThrow(() -> new EjercicioNoEncontradoException(ejercicioId));
            verificarInmutables(anterior, cambios);
            Ejercicio nuevo = anterior.toBuilder()
                    .bloqueado(EjercicioValidaciones.valor(cambios.getBloqueado(), anterior.getBloqueado()))
                    .ordenContableResultado(EjercicioValidaciones.valor(cambios.getOrdenContableResultado(), anterior.getOrdenContableResultado()))
                    .ordenContableBienesUso(EjercicioValidaciones.valor(cambios.getOrdenContableBienesUso(), anterior.getOrdenContableBienesUso()))
                    .build();
            if (nuevo.equals(anterior)) {
                return anterior;
            }
            Ejercicio guardado = repository.update(nuevo);
            historial.registrarEdicion(anterior, guardado);
            return guardado;
        }));
    }

    /** VB6 reenvía todos los campos: lo igual a lo guardado se acepta, lo distinto se rechaza. */
    private static void verificarInmutables(Ejercicio anterior, Ejercicio cambios) {
        rechazarSiDistinto("ejercicioId", cambios.getEjercicioId(), anterior.getEjercicioId());
        rechazarSiDistinto("nombre", cambios.getNombre(), anterior.getNombre());
        rechazarSiDistinto("fechaInicio", cambios.getFechaInicio(), anterior.getFechaInicio());
        rechazarSiDistinto("fechaFinal", cambios.getFechaFinal(), anterior.getFechaFinal());
    }

    private static void rechazarSiDistinto(String campo, Object enviado, Object guardado) {
        if (enviado != null && !Objects.equals(enviado, guardado)) {
            throw new EjercicioConflictoException(Codigo.CAMPO_INMUTABLE,
                    "El campo " + campo + " del ejercicio no se puede modificar");
        }
    }
}
