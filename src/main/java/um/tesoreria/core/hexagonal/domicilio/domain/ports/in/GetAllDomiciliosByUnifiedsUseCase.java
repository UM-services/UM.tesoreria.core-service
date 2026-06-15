package um.tesoreria.core.hexagonal.domicilio.domain.ports.in;

import um.tesoreria.core.model.view.DomicilioKey;

import java.util.List;

public interface GetAllDomiciliosByUnifiedsUseCase {
    List<DomicilioKey> getAllByUnifieds(List<String> unifieds);
}
