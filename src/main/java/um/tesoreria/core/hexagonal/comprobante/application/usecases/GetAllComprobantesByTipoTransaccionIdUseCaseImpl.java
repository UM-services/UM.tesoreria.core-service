package um.tesoreria.core.hexagonal.comprobante.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.comprobante.domain.ports.in.GetAllComprobantesByTipoTransaccionIdUseCase;
import um.tesoreria.core.hexagonal.comprobante.domain.ports.out.ComprobanteRepository;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllComprobantesByTipoTransaccionIdUseCaseImpl implements GetAllComprobantesByTipoTransaccionIdUseCase {
    private final ComprobanteRepository comprobanteRepository;
    @Override
    public List<Comprobante> getAllComprobantesByTipoTransaccionId(Integer tipoTransaccionId) {
        return comprobanteRepository.findAllByTipoTransaccionId(tipoTransaccionId);
    }
}
