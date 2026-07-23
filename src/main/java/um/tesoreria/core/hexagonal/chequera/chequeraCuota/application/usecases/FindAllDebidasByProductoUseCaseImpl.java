package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.FindAllDebidasByProductoUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.out.ChequeraCuotaRepository;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FindAllDebidasByProductoUseCaseImpl implements FindAllDebidasByProductoUseCase {

    private final ChequeraCuotaRepository repository;
    private final ChequeraSerieService chequeraSerieService;

    @Override
    public List<ChequeraCuota> findAllDebidasByProducto(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, Integer productoId, OffsetDateTime referencia) {
        ChequeraSerie chequeraSerie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);

        List<ChequeraCuota> todasCuotas = repository.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId);
        todasCuotas.forEach(cuota -> cuota.setChequeraId(chequeraSerie.getChequeraId()));
        repository.saveAll(todasCuotas);

        return repository.findAllDebidasByProducto(facultadId, tipoChequeraId, chequeraSerieId, alternativaId, productoId, referencia);
    }
}
