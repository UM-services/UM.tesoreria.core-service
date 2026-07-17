package um.tesoreria.core.hexagonal.comprobante.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.comprobante.domain.ports.in.GetComprobanteByTipoTransaccionIdUseCase;
import um.tesoreria.core.hexagonal.comprobante.domain.ports.out.ComprobanteRepository;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetComprobanteByTipoTransaccionIdUseCaseImpl implements GetComprobanteByTipoTransaccionIdUseCase {
    private final ComprobanteRepository comprobanteRepository;
    @Override
    public Optional<Comprobante> getComprobanteByTipoTransaccionId(Integer tipoTransaccionId) {
        return comprobanteRepository.findFirstByTipoTransaccionId(tipoTransaccionId);
    }
}
