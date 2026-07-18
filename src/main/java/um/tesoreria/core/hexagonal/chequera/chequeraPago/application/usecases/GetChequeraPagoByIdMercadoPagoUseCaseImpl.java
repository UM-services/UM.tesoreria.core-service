package um.tesoreria.core.hexagonal.chequera.chequeraPago.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in.GetChequeraPagoByIdMercadoPagoUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.out.ChequeraPagoRepository;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetChequeraPagoByIdMercadoPagoUseCaseImpl implements GetChequeraPagoByIdMercadoPagoUseCase {

    private final ChequeraPagoRepository repository;

    @Override
    public Optional<ChequeraPago> getChequeraPagoByIdMercadoPago(String idMercadoPago) {
        log.debug("Processing GetChequeraPagoByIdMercadoPagoUseCaseImpl -> {}", idMercadoPago);
        return repository.findByIdMercadoPago(idMercadoPago);
    }

}
