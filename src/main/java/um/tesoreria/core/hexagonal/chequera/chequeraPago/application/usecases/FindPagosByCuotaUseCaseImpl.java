package um.tesoreria.core.hexagonal.chequera.chequeraPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in.FindPagosByCuotaUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.out.ChequeraPagoRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FindPagosByCuotaUseCaseImpl implements FindPagosByCuotaUseCase {

    private final ChequeraPagoRepository repository;

    @Override
    public List<ChequeraPago> findPagosByCuota(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                                Integer productoId, Integer alternativaId, Integer cuotaId) {
        return repository.findAllByCuota(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId);
    }

}
