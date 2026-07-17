package um.tesoreria.core.hexagonal.comprobante.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.comprobante.domain.ports.in.GetAllComprobantesByOrdenPagoAndTipoTransaccionIdUseCase;
import um.tesoreria.core.hexagonal.comprobante.domain.ports.out.ComprobanteRepository;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllComprobantesByOrdenPagoAndTipoTransaccionIdUseCaseImpl implements GetAllComprobantesByOrdenPagoAndTipoTransaccionIdUseCase {
    private final ComprobanteRepository comprobanteRepository;
    @Override
    public List<Comprobante> getAllComprobantesByOrdenPagoAndTipoTransaccionId(Integer tipoTransaccionId) {
        return comprobanteRepository.findAllByOrdenPagoAndTipoTransaccionId(tipoTransaccionId);
    }
}
