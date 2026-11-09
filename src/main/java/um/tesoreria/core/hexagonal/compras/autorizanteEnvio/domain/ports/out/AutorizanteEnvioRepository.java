package um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.ports.out;

import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.model.AutorizanteEnvio;

import java.util.List;
import java.util.Optional;

public interface AutorizanteEnvioRepository {

    Optional<AutorizanteEnvio> findByUsuarioId(Integer usuarioId);

    List<AutorizanteEnvio> findAll();

    AutorizanteEnvio save(AutorizanteEnvio autorizante);

    boolean existe(Integer usuarioId, Integer dependenciaId);

}
