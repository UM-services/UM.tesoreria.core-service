package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.CalculateDeudaUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in.GetChequeraSerieByUniqueUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.out.ChequeraSerieRepository;
import um.tesoreria.core.service.ChequeraImpresionCabeceraService;

import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetChequeraSerieByUniqueUseCaseImpl implements GetChequeraSerieByUniqueUseCase {

    private final ChequeraSerieRepository repository;
    private final CalculateDeudaUseCase calculateDeudaUseCase;
    private final ChequeraImpresionCabeceraService chequeraImpresionCabeceraService;

    @Override
    public Optional<ChequeraSerie> getByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        return repository.findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId)
                .map(chequera -> {
                    setUltimoEnvio(chequera);
                    return chequera;
                });
    }

    @Override
    public Optional<ChequeraSerie> getByUniqueExtended(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        return getByUnique(facultadId, tipoChequeraId, chequeraSerieId)
                .map(chequera -> {
                    setDeuda(chequera);
                    return chequera;
                });
    }

    private void setDeuda(ChequeraSerie chequera) {
        var deuda = calculateDeudaUseCase.calculateDeuda(chequera);
        chequera.setCuotasDeuda(deuda.getCuotas());
        chequera.setImporteDeuda(deuda.getDeuda());
    }

    private void setUltimoEnvio(ChequeraSerie chequera) {
        var ultimoEnvio = chequeraImpresionCabeceraService.findLastByUnique(
                chequera.getFacultadId(),
                chequera.getTipoChequeraId(),
                chequera.getChequeraSerieId()
        )
        .map(cabecera -> Objects.requireNonNull(cabecera.getFecha()).plusHours(-3))
        .orElse(null);

        chequera.setUltimoEnvio(ultimoEnvio);
    }
}
