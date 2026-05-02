package um.tesoreria.core.hexagonal.geografica.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.geografica.domain.model.Geografica;
import um.tesoreria.core.hexagonal.geografica.domain.ports.in.GetAllGeograficasUseCase;
import um.tesoreria.core.hexagonal.geografica.domain.ports.out.GeograficaRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllGeograficasUseCaseImpl implements GetAllGeograficasUseCase {

    private final GeograficaRepository geograficaRepository;

    @Override
    public List<Geografica> getAllGeograficas() {
        return geograficaRepository.findAll();
    }

}
