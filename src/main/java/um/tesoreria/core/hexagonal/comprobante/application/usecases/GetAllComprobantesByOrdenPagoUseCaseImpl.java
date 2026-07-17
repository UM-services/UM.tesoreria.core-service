package um.tesoreria.core.hexagonal.comprobante.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.comprobante.domain.ports.in.GetAllComprobantesByOrdenPagoUseCase;
import um.tesoreria.core.hexagonal.comprobante.domain.ports.out.ComprobanteRepository;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllComprobantesByOrdenPagoUseCaseImpl implements GetAllComprobantesByOrdenPagoUseCase {
    private final ComprobanteRepository comprobanteRepository;
    @Override
    public List<Comprobante> getAllComprobantesByOrdenPago() {
        return comprobanteRepository.findAllByOrdenPago();
    }
}
