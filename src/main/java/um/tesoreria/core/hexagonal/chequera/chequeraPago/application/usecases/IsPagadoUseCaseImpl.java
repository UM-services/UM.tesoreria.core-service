package um.tesoreria.core.hexagonal.chequera.chequeraPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in.IsPagadoUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.out.ChequeraPagoRepository;

@Component
@RequiredArgsConstructor
public class IsPagadoUseCaseImpl implements IsPagadoUseCase {

    private final ChequeraPagoRepository repository;

    @Override
    public boolean isPagado(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                            Integer productoId, Integer alternativaId, Integer cuotaId) {
        return !repository.findAllByCuota(facultadId, tipoChequeraId, chequeraSerieId,
                productoId, alternativaId, cuotaId).isEmpty();
    }

}
