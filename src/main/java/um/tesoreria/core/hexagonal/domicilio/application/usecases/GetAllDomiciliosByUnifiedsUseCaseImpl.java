package um.tesoreria.core.hexagonal.domicilio.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.domicilio.domain.ports.in.GetAllDomiciliosByUnifiedsUseCase;
import um.tesoreria.core.model.view.DomicilioKey;
import um.tesoreria.core.service.view.DomicilioKeyService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllDomiciliosByUnifiedsUseCaseImpl implements GetAllDomiciliosByUnifiedsUseCase {

    private final DomicilioKeyService domicilioKeyService;

    @Override
    public List<DomicilioKey> getAllByUnifieds(List<String> unifieds) {
        return domicilioKeyService.findAllByUnifiedIn(unifieds);
    }
}
