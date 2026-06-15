package um.tesoreria.core.hexagonal.domicilio.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.domicilio.domain.ports.in.DeleteDomicilioUseCase;
import um.tesoreria.core.hexagonal.domicilio.domain.ports.out.DomicilioRepository;

@Component
@RequiredArgsConstructor
public class DeleteDomicilioUseCaseImpl implements DeleteDomicilioUseCase {
    private final DomicilioRepository repository;

    @Override
    public boolean deleteDomicilio(Long id) {
        return repository.deleteById(id);
    }
}
