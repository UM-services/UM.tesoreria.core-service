package um.tesoreria.core.hexagonal.mercadoPagoContext.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.mercadoPagoContext.application.service.VencimientoMercadoPagoResolver;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.RenovacionMercadoPago;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in.FindRenovacionesUseCase;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.out.MercadoPagoContextRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FindRenovacionesUseCaseImpl implements FindRenovacionesUseCase {

    private final MercadoPagoContextRepository repository;
    private final VencimientoMercadoPagoResolver resolver;

    @Override
    public List<RenovacionMercadoPago> findRenovaciones(OffsetDateTime hoy, int lookbackDias, boolean reintentos,
                                                       OffsetDateTime afterFecha, Long afterId, int size) {
        var desde = hoy.minusDays(lookbackDias);
        return repository.findRenovaciones(desde, hoy, reintentos, afterFecha, afterId, size)
                .stream()
                .map(contexto -> {
                    var cuota = contexto.getChequeraCuota();
                    if (cuota == null) {
                        return null;
                    }
                    var vencimiento = resolver.seleccionar(cuota, hoy);
                    return new RenovacionMercadoPago(contexto, cuota,
                            vencimiento.fechaVencimiento(), vencimiento.importe());
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
