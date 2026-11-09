package um.tesoreria.core.hexagonal.compras.autorizanteEnvio.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.application.exception.AutorizanteEnvioException;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.model.AutorizanteEnvio;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.ports.in.ReemplazarDependenciasAutorizanteUseCase;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.ports.out.AutorizanteEnvioRepository;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ReemplazarDependenciasAutorizanteUseCaseImpl implements ReemplazarDependenciasAutorizanteUseCase {

    private final AutorizanteEnvioRepository autorizanteEnvioRepository;

    @Override
    public AutorizanteEnvio reemplazar(Integer usuarioId, Collection<Integer> dependenciaIds) {
        if (usuarioId == null) {
            throw new AutorizanteEnvioException("Falta el usuario autorizante");
        }
        Set<Integer> ids = new LinkedHashSet<>();
        if (dependenciaIds != null) {
            dependenciaIds.stream()
                    .filter(Objects::nonNull)
                    .forEach(ids::add);
        }
        return autorizanteEnvioRepository.save(new AutorizanteEnvio(usuarioId, ids));
    }

}
