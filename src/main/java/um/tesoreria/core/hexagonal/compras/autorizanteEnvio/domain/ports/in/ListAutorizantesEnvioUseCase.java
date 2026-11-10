package um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.model.AutorizanteEnvio;

import java.util.List;

public interface ListAutorizantesEnvioUseCase {

    List<AutorizanteEnvio> listar();

}
