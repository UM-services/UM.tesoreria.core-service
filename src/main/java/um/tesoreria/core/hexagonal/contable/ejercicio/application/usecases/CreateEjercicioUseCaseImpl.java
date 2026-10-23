package um.tesoreria.core.hexagonal.contable.ejercicio.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioConflictoException;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioConflictoException.Codigo;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioSolicitudInvalidaException;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.EjercicioSiguiente;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.in.CreateEjercicioUseCase;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out.EjercicioHistorialPort;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out.EjercicioRepository;

import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CreateEjercicioUseCaseImpl implements CreateEjercicioUseCase {
    private final EjercicioRepository repository;
    private final EjercicioHistorialPort historial;
    private final TransactionOperations transaction;
    private final Clock clock;

    @Override
    public Ejercicio createEjercicio(Ejercicio pedido) {
        // VB6 deja en blanco o en cero lo que no define: un nombre vacío o un id <= 0 equivalen a «omitido».
        Ejercicio solicitado = EjercicioValidaciones.sinSentinelas(pedido);
        EjercicioValidaciones.validar(solicitado);
        return ReintentoPorBloqueo.ejecutar(() -> transaction.execute(status -> {
            // Primera lectura de la transacción: bloquea el último y serializa altas y bajas.
            Optional<Ejercicio> ultimo = repository.findLastWithLock();
            Ejercicio nuevo = ultimo.map(u -> derivar(u, solicitado)).orElseGet(() -> primero(solicitado));
            Ejercicio creado = repository.create(nuevo);
            historial.registrarAlta(creado);
            return creado;
        }));
    }

    private Ejercicio derivar(Ejercicio ultimo, Ejercicio solicitado) {
        if (EjercicioSiguiente.superaLimite(ultimo, LocalDate.now(clock))) {
            throw new EjercicioConflictoException(Codigo.LIMITE_ALTA,
                    "No se pueden crear más ejercicios: el último inicia en " + ultimo.getFechaInicio().getYear());
        }
        Ejercicio esperado = EjercicioSiguiente.despuesDe(ultimo);
        if (!ultimo.getFechaFinal().plusDays(1).equals(esperado.getFechaInicio())) {
            throw new EjercicioConflictoException(Codigo.SALTO, "El ejercicio " + ultimo.getEjercicioId()
                    + " termina el " + ultimo.getFechaFinal() + ": el siguiente tendría que empezar el "
                    + ultimo.getFechaFinal().plusDays(1) + " y la regla da " + esperado.getFechaInicio());
        }
        Integer id = solicitado.getEjercicioId();
        if (id != null && !id.equals(esperado.getEjercicioId())) {
            throw id <= ultimo.getEjercicioId()
                    ? new EjercicioConflictoException(Codigo.DUPLICADO, "El ejercicio " + id + " ya existe")
                    : new EjercicioConflictoException(Codigo.SALTO,
                            "El ejercicio siguiente es el " + esperado.getEjercicioId() + ", no el " + id);
        }
        verificarFecha("fechaInicio", solicitado.getFechaInicio(), esperado.getFechaInicio());
        verificarFecha("fechaFinal", solicitado.getFechaFinal(), esperado.getFechaFinal());
        if (solicitado.getNombre() != null && !solicitado.getNombre().equals(esperado.getNombre())) {
            throw new EjercicioConflictoException(Codigo.CAMPO_INMUTABLE,
                    "El nombre del ejercicio siguiente es '" + esperado.getNombre() + "'");
        }
        return esperado.toBuilder()
                .bloqueado(EjercicioValidaciones.valor(solicitado.getBloqueado(), esperado.getBloqueado()))
                .ordenContableResultado(EjercicioValidaciones.valor(solicitado.getOrdenContableResultado(), 0))
                .ordenContableBienesUso(EjercicioValidaciones.valor(solicitado.getOrdenContableBienesUso(), 0))
                .build();
    }

    private static void verificarFecha(String campo, LocalDate enviada, LocalDate esperada) {
        if (enviada == null || enviada.equals(esperada)) {
            return;
        }
        throw enviada.isBefore(esperada)
                ? new EjercicioConflictoException(Codigo.DUPLICADO,
                        campo + " " + enviada + " se superpone con un ejercicio existente; se esperaba " + esperada)
                : new EjercicioConflictoException(Codigo.SALTO,
                        campo + " " + enviada + " salta un período; se esperaba " + esperada);
    }

    /** Sin ejercicios previos no hay nada que derivar: el alta tiene que traer id y fechas. */
    private Ejercicio primero(Ejercicio solicitado) {
        if (solicitado.getEjercicioId() == null || solicitado.getFechaInicio() == null
                || solicitado.getFechaFinal() == null) {
            throw new EjercicioSolicitudInvalidaException(
                    "Sin ejercicios previos el alta requiere ejercicioId, fechaInicio y fechaFinal");
        }
        if (!solicitado.getFechaInicio().isBefore(solicitado.getFechaFinal())) {
            throw new EjercicioSolicitudInvalidaException("fechaInicio debe ser anterior a fechaFinal");
        }
        if (!EjercicioSiguiente.esPeriodoRegular(solicitado.getFechaInicio(), solicitado.getFechaFinal())) {
            throw new EjercicioSolicitudInvalidaException(
                    "El primer ejercicio tiene que ir del 1 de marzo al último día de febrero siguiente, para que las altas posteriores continúen");
        }
        String nombre = solicitado.getNombre() == null
                ? EjercicioSiguiente.nombrePara(solicitado.getFechaInicio()) : solicitado.getNombre();
        return solicitado.toBuilder()
                .nombre(nombre)
                .bloqueado(EjercicioValidaciones.valor(solicitado.getBloqueado(), (byte) 0))
                .ordenContableResultado(EjercicioValidaciones.valor(solicitado.getOrdenContableResultado(), 0))
                .ordenContableBienesUso(EjercicioValidaciones.valor(solicitado.getOrdenContableBienesUso(), 0))
                .build();
    }

}
