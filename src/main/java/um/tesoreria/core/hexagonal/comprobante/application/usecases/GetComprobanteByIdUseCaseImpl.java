package um.tesoreria.core.hexagonal.comprobante.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.comprobante.domain.ports.in.GetComprobanteByIdUseCase;
import um.tesoreria.core.hexagonal.comprobante.domain.ports.out.ComprobanteRepository;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetComprobanteByIdUseCaseImpl implements GetComprobanteByIdUseCase {
    private final ComprobanteRepository comprobanteRepository;
    @Override
    public Optional<Comprobante> getComprobanteById(Integer comprobanteId) {
        return comprobanteRepository.findByComprobanteId(comprobanteId);
    }
}
