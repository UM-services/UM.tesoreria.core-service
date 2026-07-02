package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.usecases;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.FindAllInconsistenciasUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.infrastructure.persistence.mapper.ChequeraCuotaMapper;
import um.tesoreria.core.kotlin.model.view.ChequeraCuotaDeuda;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaDeudaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.AbstractMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class FindAllInconsistenciasUseCaseImpl implements FindAllInconsistenciasUseCase {

    private final ChequeraCuotaDeudaService chequeraCuotaDeudaService;
    private final ChequeraCuotaMapper mapper;
    private final ChequeraCuotaService chequeraCuotaService;

    public FindAllInconsistenciasUseCaseImpl(ChequeraCuotaDeudaService chequeraCuotaDeudaService, ChequeraCuotaMapper mapper, @Lazy ChequeraCuotaService chequeraCuotaService) {
        this.chequeraCuotaDeudaService = chequeraCuotaDeudaService;
        this.mapper = mapper;
        this.chequeraCuotaService = chequeraCuotaService;
    }

    @Override
    public List<ChequeraCuota> findAllInconsistencias(OffsetDateTime desde, OffsetDateTime hasta, Boolean reduced) {
        final BigDecimal MULTIPLICADOR = new BigDecimal(49);

        return chequeraCuotaDeudaService.findAllByRango(desde, hasta, reduced, null, chequeraCuotaService).stream()
                .map(ChequeraCuotaDeuda::getChequeraCuota)
                .filter(Objects::nonNull)
                .map(mapper::toDomain)
                .filter(cuota -> {
                    boolean vencimientosInvalidos = Objects.requireNonNull(cuota.getVencimiento1())
                            .isAfter(cuota.getVencimiento2()) ||
                            Objects.requireNonNull(cuota.getVencimiento2()).isAfter(cuota.getVencimiento3());

                    boolean importesInvalidos = cuota.getImporte1().compareTo(cuota.getImporte2()) > 0 ||
                            cuota.getImporte2().compareTo(cuota.getImporte3()) > 0;

                    boolean multiplicadoresInvalidos = Stream.of(
                            new AbstractMap.SimpleEntry<>(cuota.getImporte1Original(), cuota.getImporte1()),
                            new AbstractMap.SimpleEntry<>(cuota.getImporte2Original(), cuota.getImporte2()),
                            new AbstractMap.SimpleEntry<>(cuota.getImporte3Original(), cuota.getImporte3()))
                            .anyMatch(entry -> entry.getKey().multiply(MULTIPLICADOR).compareTo(entry.getValue()) < 0);

                    return vencimientosInvalidos || importesInvalidos || multiplicadoresInvalidos;
                })
                .collect(Collectors.toList());
    }
}
