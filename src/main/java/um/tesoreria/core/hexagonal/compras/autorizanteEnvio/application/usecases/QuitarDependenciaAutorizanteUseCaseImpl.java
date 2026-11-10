package um.tesoreria.core.hexagonal.compras.autorizanteEnvio.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.application.exception.AutorizanteEnvioException;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.model.AutorizanteEnvio;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.ports.in.QuitarDependenciaAutorizanteUseCase;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.ports.out.AutorizanteEnvioRepository;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class QuitarDependenciaAutorizanteUseCaseImpl implements QuitarDependenciaAutorizanteUseCase {

    private final AutorizanteEnvioRepository autorizanteEnvioRepository;

    @Override
    public AutorizanteEnvio quitar(Integer usuarioId, Integer dependenciaId) {
        if (usuarioId == null || dependenciaId == null) {
            throw new AutorizanteEnvioException("La baja requiere usuario y dependencia");
        }
        AutorizanteEnvio actual = autorizanteEnvioRepository.findByUsuarioId(usuarioId)
                .orElse(new AutorizanteEnvio(usuarioId, Set.of()));
        return autorizanteEnvioRepository.save(actual.quitar(dependenciaId));
    }

}
