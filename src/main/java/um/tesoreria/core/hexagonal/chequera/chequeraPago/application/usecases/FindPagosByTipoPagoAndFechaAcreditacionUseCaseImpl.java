package um.tesoreria.core.hexagonal.chequera.chequeraPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in.FindPagosByTipoPagoAndFechaAcreditacionUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.out.ChequeraPagoRepository;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FindPagosByTipoPagoAndFechaAcreditacionUseCaseImpl implements FindPagosByTipoPagoAndFechaAcreditacionUseCase {

    private final ChequeraPagoRepository repository;

    @Override
    public List<ChequeraPago> findPagosByTipoPagoAndFechaAcreditacion(Integer tipoPagoId, OffsetDateTime fechaAcreditacion) {
        return repository.findAllByTipoPagoAndAcreditacion(tipoPagoId, fechaAcreditacion);
    }

}
