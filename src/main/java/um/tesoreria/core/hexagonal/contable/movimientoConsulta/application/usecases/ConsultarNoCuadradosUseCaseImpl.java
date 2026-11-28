package um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.service.ArmadoAsientos;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.service.LecturaRango;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.AsientoNoCuadrado;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.AsientosNoCuadrados;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.ports.in.ConsultarNoCuadradosUseCase;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * "Diferencia" de {@code frmMovimientosContables}: los asientos de un rango cuyo debe difiere del haber. Lee y arma
 * igual que el Libro Diario, con sus topes y errores. Un asiento sin líneas no existe en {@code movcon}.
 */
@Component
@RequiredArgsConstructor
public class ConsultarNoCuadradosUseCaseImpl implements ConsultarNoCuadradosUseCase {

    private final LecturaRango lecturaRango;
    private final ArmadoAsientos armadoAsientos;

    @Override
    @Transactional(readOnly = true)
    public AsientosNoCuadrados consultarNoCuadrados(OffsetDateTime desde, OffsetDateTime hasta) {
        return new AsientosNoCuadrados(desde, hasta, armadoAsientos.asientos(lecturaRango.leer(desde, hasta)).stream()
                .map(AsientoNoCuadrado::de)
                .flatMap(Optional::stream)
                .toList());
    }
}
