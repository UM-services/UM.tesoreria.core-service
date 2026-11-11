package um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.model.AutorizanteEnvio;

import java.util.Collection;

public interface ReemplazarDependenciasAutorizanteUseCase {

    AutorizanteEnvio reemplazar(Integer usuarioId, Collection<Integer> dependenciaIds);

}
