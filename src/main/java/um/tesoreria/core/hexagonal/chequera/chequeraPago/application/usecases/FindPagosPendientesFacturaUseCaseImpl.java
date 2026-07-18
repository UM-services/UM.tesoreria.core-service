package um.tesoreria.core.hexagonal.chequera.chequeraPago.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in.FindPagosPendientesFacturaUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.out.ChequeraPagoRepository;
import um.tesoreria.core.model.FacturacionElectronica;
import um.tesoreria.core.service.FacturacionElectronicaService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FindPagosPendientesFacturaUseCaseImpl implements FindPagosPendientesFacturaUseCase {

    private static final int TIPO_PAGO_THRESHOLD = 2;

    private final ChequeraPagoRepository repository;
    private final FacturacionElectronicaService facturacionElectronicaService;

    @Override
    public List<ChequeraPago> findPagosPendientesFactura(OffsetDateTime fechaPago) {
        OffsetDateTime fechaInicio = fechaPago.withHour(0).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime fechaFin = fechaPago.withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        List<ChequeraPago> pagos = repository.findPendientesFactura(fechaInicio, fechaFin, TIPO_PAGO_THRESHOLD);

        List<Long> pagoIds = pagos.stream()
                .map(ChequeraPago::getChequeraPagoId)
                .collect(Collectors.toList());

        Map<Long, FacturacionElectronica> electronicas = facturacionElectronicaService
                .findAllByChequeraPagoIds(pagoIds)
                .stream()
                .collect(Collectors.toMap(
                        FacturacionElectronica::getChequeraPagoId,
                        Function.identity(),
                        (existing, replacement) -> existing
                ));

        return pagos.stream()
                .filter(pago -> !electronicas.containsKey(pago.getChequeraPagoId()))
                .collect(Collectors.toList());
    }

}
