package um.tesoreria.core.hexagonal.chequera.chequeraPago.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in.GetNextOrdenUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.out.ChequeraPagoRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetNextOrdenUseCaseImpl implements GetNextOrdenUseCase {

    private final ChequeraPagoRepository repository;

    @Override
    public Integer getNextOrden(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                Integer productoId, Integer alternativaId, Integer cuotaId) {
        log.debug("Processing getNextOrden");
        return repository.findTopByCompositeKeyOrderByOrdenDesc(facultadId, tipoChequeraId, chequeraSerieId,
                productoId, alternativaId, cuotaId)
                .map(pago -> pago.getOrden() + 1)
                .orElse(0);
    }

}
