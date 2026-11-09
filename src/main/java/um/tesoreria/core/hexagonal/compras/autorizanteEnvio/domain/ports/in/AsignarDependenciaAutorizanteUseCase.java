package um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.model.AutorizanteEnvio;

public interface AsignarDependenciaAutorizanteUseCase {

    AutorizanteEnvio asignar(Integer usuarioId, Integer dependenciaId);

}
