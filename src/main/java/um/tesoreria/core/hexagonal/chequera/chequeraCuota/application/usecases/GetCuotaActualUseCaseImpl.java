package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.GetCuotaActualUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.out.ChequeraCuotaRepository;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;

import java.time.OffsetDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetCuotaActualUseCaseImpl implements GetCuotaActualUseCase {

    private final ChequeraCuotaRepository repository;
    private final ChequeraSerieService chequeraSerieService;

    @Override
    public Optional<ChequeraCuota> getCuotaActual(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                                   Integer productoId, Integer alternativaId, OffsetDateTime fechaActual) {
        var cuotas = repository.findAllByChequeraAlternativa(facultadId, tipoChequeraId, chequeraSerieId, alternativaId);
        Optional<ChequeraCuota> cuotaActual = cuotas.stream()
                .filter(cuota -> isBeforeAnyVencimiento(fechaActual, cuota))
                .findFirst();
        if (cuotaActual.isPresent()) {
            ChequeraCuota chequeraCuota = cuotaActual.get();
            if (chequeraCuota.getChequeraId() == null) {
                ChequeraSerie chequeraSerie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
                chequeraCuota.setChequeraId(chequeraSerie.getChequeraId());
                chequeraCuota = repository.save(chequeraCuota);
            }
            return Optional.of(chequeraCuota);
        }
        return Optional.empty();
    }

    private boolean isBeforeAnyVencimiento(OffsetDateTime fechaActual, ChequeraCuota cuota) {
        return (cuota.getVencimiento1() != null && fechaActual.isBefore(cuota.getVencimiento1()))
                || (cuota.getVencimiento2() != null && fechaActual.isBefore(cuota.getVencimiento2()));
    }
}
