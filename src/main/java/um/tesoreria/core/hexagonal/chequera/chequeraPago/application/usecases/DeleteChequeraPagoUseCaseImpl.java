package um.tesoreria.core.hexagonal.chequera.chequeraPago.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in.DeleteChequeraPagoUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.out.ChequeraPagoRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteChequeraPagoUseCaseImpl implements DeleteChequeraPagoUseCase {

    private final ChequeraPagoRepository repository;

    @Override
    public void deleteChequeraPago(Long chequeraPagoId) {
        log.debug("Processing DeleteChequeraPagoUseCaseImpl -> {}", chequeraPagoId);
        repository.deleteByChequeraPagoId(chequeraPagoId);
    }

}