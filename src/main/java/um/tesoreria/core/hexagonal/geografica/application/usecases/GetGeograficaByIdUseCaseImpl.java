package um.tesoreria.core.hexagonal.geografica.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.geografica.domain.model.Geografica;
import um.tesoreria.core.hexagonal.geografica.domain.ports.in.GetGeograficaByIdUseCase;
import um.tesoreria.core.hexagonal.geografica.domain.ports.out.GeograficaRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetGeograficaByIdUseCaseImpl implements GetGeograficaByIdUseCase {

    private final GeograficaRepository geograficaRepository;

    @Override
    public Optional<Geografica> getGeograficaById(Integer geograficaId) {
        return geograficaRepository.findByGeograficaId(geograficaId);
    }

}
