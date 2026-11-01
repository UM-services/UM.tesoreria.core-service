package um.tesoreria.core.hexagonal.mercadoPagoContext.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in.FindAllActiveToChangeUseCase;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.out.MercadoPagoContextRepository;
import um.tesoreria.core.util.Tool;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FindAllActiveToChangeUseCaseImpl implements FindAllActiveToChangeUseCase {

    private final MercadoPagoContextRepository repository;

    @Override
    public List<Long> findAllActiveToChange() {
        var today = Tool.dateAbsoluteArgentina();
        // Flujo legado: conserva su ventana de 90 días. El proceso vigente (renovaciones) usa 72 h.
        var _90DaysAgo = today.minusDays(90);
        return Objects.requireNonNull(repository.findAllByActivoAndFechaVencimientoBetween((byte) 1, _90DaysAgo, today))
                .stream()
                .filter(Objects::nonNull)
                .map(MercadoPagoContext::getChequeraCuotaId)
                // Los contextos de reserva de vacante no tienen cuota y antes abortaban el barrido.
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }
}
