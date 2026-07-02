package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in.GetChequeraSerieByCbuOrVisaUseCase;
import um.tesoreria.core.service.DebitoService;
import um.tesoreria.core.service.view.ChequeraKeyService;
import um.tesoreria.core.model.Debito;
import um.tesoreria.core.model.view.ChequeraKey;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetChequeraSerieByCbuOrVisaUseCaseImpl implements GetChequeraSerieByCbuOrVisaUseCase {

    private final DebitoService debitoService;
    private final ChequeraKeyService chequeraKeyService;

    @Override
    public List<ChequeraKey> getAllByCbu(String cbu, Integer debitoTipoId) {
        return chequeraKeyService.findAllByChequeraKey(debitoService.findAllByCbu(cbu, debitoTipoId).stream()
                .map(Debito::chequeraKey).collect(Collectors.toList()));
    }

    @Override
    public List<ChequeraKey> getAllByVisa(String numeroTarjeta, Integer debitoTipoId) {
        return chequeraKeyService.findAllByChequeraKey(debitoService.findAllByVisa(numeroTarjeta, debitoTipoId).stream()
                .map(Debito::chequeraKey).collect(Collectors.toList()));
    }
}
