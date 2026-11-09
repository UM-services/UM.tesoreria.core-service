package um.tesoreria.core.hexagonal.compras.autorizanteEnvio.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.model.AutorizanteEnvio;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.ports.in.ListAutorizantesEnvioUseCase;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.ports.out.AutorizanteEnvioRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListAutorizantesEnvioUseCaseImpl implements ListAutorizantesEnvioUseCase {

    private final AutorizanteEnvioRepository autorizanteEnvioRepository;

    @Override
    public List<AutorizanteEnvio> listar() {
        return autorizanteEnvioRepository.findAll();
    }

}
