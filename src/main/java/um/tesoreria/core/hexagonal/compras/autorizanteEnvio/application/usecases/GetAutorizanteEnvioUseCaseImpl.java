package um.tesoreria.core.hexagonal.compras.autorizanteEnvio.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.application.exception.AutorizanteEnvioException;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.model.AutorizanteEnvio;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.ports.in.GetAutorizanteEnvioUseCase;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.ports.out.AutorizanteEnvioRepository;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class GetAutorizanteEnvioUseCaseImpl implements GetAutorizanteEnvioUseCase {

    private final AutorizanteEnvioRepository autorizanteEnvioRepository;

    @Override
    public AutorizanteEnvio get(Integer usuarioId) {
        if (usuarioId == null) {
            throw new AutorizanteEnvioException("Falta el usuario autorizante");
        }
        return autorizanteEnvioRepository.findByUsuarioId(usuarioId)
                .orElse(new AutorizanteEnvio(usuarioId, Set.of()));
    }

}
