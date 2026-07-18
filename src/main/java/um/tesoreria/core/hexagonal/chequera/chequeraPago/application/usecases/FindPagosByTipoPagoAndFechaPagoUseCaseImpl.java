package um.tesoreria.core.hexagonal.chequera.chequeraPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in.FindPagosByTipoPagoAndFechaPagoUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.out.ChequeraPagoRepository;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FindPagosByTipoPagoAndFechaPagoUseCaseImpl implements FindPagosByTipoPagoAndFechaPagoUseCase {

    private final ChequeraPagoRepository repository;

    @Override
    public List<ChequeraPago> findPagosByTipoPagoAndFechaPago(Integer tipoPagoId, OffsetDateTime fechaPago) {
        OffsetDateTime fechaInicio = fechaPago.withHour(0).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime fechaFin = fechaPago.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        return repository.findAllByTipoPagoAndFechaBetween(tipoPagoId, fechaInicio, fechaFin);
    }

}
