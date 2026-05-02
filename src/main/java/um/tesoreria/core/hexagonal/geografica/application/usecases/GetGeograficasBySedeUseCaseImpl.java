package um.tesoreria.core.hexagonal.geografica.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.geografica.domain.model.Geografica;
import um.tesoreria.core.hexagonal.geografica.domain.ports.in.GetGeograficasBySedeUseCase;
import um.tesoreria.core.hexagonal.geografica.domain.ports.out.GeograficaRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetGeograficasBySedeUseCaseImpl implements GetGeograficasBySedeUseCase {

    private final GeograficaRepository geograficaRepository;

    @Override
    public List<Geografica> getGeograficasBySede(Integer geograficaId) {
        if (geograficaId == 0) {
            return geograficaRepository.findAll();
        }
        return geograficaRepository.findAllByGeograficaId(geograficaId);
    }

}
