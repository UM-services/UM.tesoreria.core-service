package um.tesoreria.core.hexagonal.compras.autorizanteEnvio.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.model.AutorizanteEnvio;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.ports.in.AsignarDependenciaAutorizanteUseCase;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.ports.in.GetAutorizanteEnvioUseCase;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.ports.in.ListAutorizantesEnvioUseCase;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.ports.in.QuitarDependenciaAutorizanteUseCase;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.ports.in.ReemplazarDependenciasAutorizanteUseCase;

import java.util.Collection;
import java.util.List;

/**
 * Fachada del contexto de autorizantes de envío: un usuario y el conjunto de dependencias
 * sobre las que puede aprobar o rechazar pedidos de compra.
 */
@Service
@RequiredArgsConstructor
public class AutorizanteEnvioService {

    private final GetAutorizanteEnvioUseCase getAutorizanteEnvioUseCase;
    private final ReemplazarDependenciasAutorizanteUseCase reemplazarDependenciasAutorizanteUseCase;
    private final AsignarDependenciaAutorizanteUseCase asignarDependenciaAutorizanteUseCase;
    private final QuitarDependenciaAutorizanteUseCase quitarDependenciaAutorizanteUseCase;
    private final ListAutorizantesEnvioUseCase listAutorizantesEnvioUseCase;

    public AutorizanteEnvio get(Integer usuarioId) {
        return getAutorizanteEnvioUseCase.get(usuarioId);
    }

    public AutorizanteEnvio reemplazar(Integer usuarioId, Collection<Integer> dependenciaIds) {
        return reemplazarDependenciasAutorizanteUseCase.reemplazar(usuarioId, dependenciaIds);
    }

    public AutorizanteEnvio asignar(Integer usuarioId, Integer dependenciaId) {
        return asignarDependenciaAutorizanteUseCase.asignar(usuarioId, dependenciaId);
    }

    public AutorizanteEnvio quitar(Integer usuarioId, Integer dependenciaId) {
        return quitarDependenciaAutorizanteUseCase.quitar(usuarioId, dependenciaId);
    }

    public List<AutorizanteEnvio> listar() {
        return listAutorizantesEnvioUseCase.listar();
    }

}
