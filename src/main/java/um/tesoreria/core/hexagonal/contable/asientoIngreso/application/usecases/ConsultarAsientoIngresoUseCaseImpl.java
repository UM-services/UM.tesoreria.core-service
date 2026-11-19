package um.tesoreria.core.hexagonal.contable.asientoIngreso.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.service.AsientoIngresoResumenBuilder;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.service.FechasContables;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoResumen;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.in.ConsultarAsientoIngresoUseCase;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out.IngresoAsientoMapaRepository;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class ConsultarAsientoIngresoUseCaseImpl implements ConsultarAsientoIngresoUseCase {

    private final IngresoAsientoMapaRepository mapaRepository;
    private final AsientoIngresoResumenBuilder resumenBuilder;

    @Override
    public AsientoIngresoResumen consultar(OffsetDateTime fecha, Integer tipoPagoId) {
        OffsetDateTime normalizada = FechasContables.normalizar(fecha);
        var registro = mapaRepository.findByFechaAndTipoPago(normalizada, tipoPagoId).orElse(null);
        return resumenBuilder.build(normalizada, tipoPagoId, registro);
    }

}
