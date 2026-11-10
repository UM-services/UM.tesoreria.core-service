package um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.model.AutorizanteEnvio;

public interface GetAutorizanteEnvioUseCase {

    AutorizanteEnvio get(Integer usuarioId);

}
