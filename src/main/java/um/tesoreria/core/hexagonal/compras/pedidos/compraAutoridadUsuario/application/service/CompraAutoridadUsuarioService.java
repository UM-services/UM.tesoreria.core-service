package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.application.service.CompraAutoridadPerfilService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.model.CompraAutoridadPerfil;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.application.exception.CompraAutoridadUsuarioException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.model.LimiteAutorizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.ports.in.AsignarAutoridadUsuarioUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.ports.in.GetPerfilIdsByUsuarioUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.ports.in.QuitarAutoridadUsuarioUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.application.service.CompraReferenciaService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.model.CompraReferencia;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Fachada de la autoridad por monto.
 *
 * <p>Composición cross-slice en la capa de aplicación (mismo patrón que
 * {@code CompraPedidoService}): compone los perfiles de autoridad ({@code compraAutoridadPerfil})
 * y la referencia del ejercicio ({@code compraReferencia}) con la asignación usuario ↔ perfil,
 * que vive en este slice. Los dominios de cada slice permanecen puros.</p>
 */
@Service
@RequiredArgsConstructor
public class CompraAutoridadUsuarioService {

    private final AsignarAutoridadUsuarioUseCase asignarAutoridadUsuarioUseCase;
    private final QuitarAutoridadUsuarioUseCase quitarAutoridadUsuarioUseCase;
    private final GetPerfilIdsByUsuarioUseCase getPerfilIdsByUsuarioUseCase;

    // Excepción cross-slice autorizada: composición de slices del subdominio "pedidos".
    private final CompraAutoridadPerfilService compraAutoridadPerfilService;
    private final CompraReferenciaService compraReferenciaService;

    @Transactional
    public void asignar(Integer usuarioId, Long autoridadPerfilId) {
        if (usuarioId == null) {
            throw new CompraAutoridadUsuarioException("usuarioId es requerido");
        }
        if (autoridadPerfilId == null) {
            throw new CompraAutoridadUsuarioException("autoridadPerfilId es requerido");
        }
        compraAutoridadPerfilService.findById(autoridadPerfilId)
                .orElseThrow(() -> new CompraAutoridadUsuarioException(
                        "No existe el perfil de autoridad con id: " + autoridadPerfilId));
        asignarAutoridadUsuarioUseCase.asignar(usuarioId, autoridadPerfilId);
    }

    @Transactional
    public void quitar(Integer usuarioId, Long autoridadPerfilId) {
        if (usuarioId == null || autoridadPerfilId == null) {
            throw new CompraAutoridadUsuarioException("usuarioId y autoridadPerfilId son requeridos");
        }
        quitarAutoridadUsuarioUseCase.quitar(usuarioId, autoridadPerfilId);
    }

    public List<Long> getPerfilIds(Integer usuarioId) {
        return getPerfilIdsByUsuarioUseCase.getPerfilIds(usuarioId);
    }

    /**
     * Resuelve el límite efectivo del usuario para el ejercicio: {@code MAX(multiplico) × referencia}.
     * No lanza si el usuario no tiene autoridad: devuelve {@code tieneAutoridad = false} para que el
     * consumidor aplique fail-closed.
     */
    public LimiteAutorizacion getLimite(Integer usuarioId, Integer ejercicioId) {
        if (usuarioId == null) {
            throw new CompraAutoridadUsuarioException("usuarioId es requerido");
        }
        if (ejercicioId == null) {
            throw new CompraAutoridadUsuarioException("ejercicioId es requerido");
        }
        List<CompraAutoridadPerfil> perfiles = getPerfilIdsByUsuarioUseCase.getPerfilIds(usuarioId).stream()
                .map(compraAutoridadPerfilService::findById)
                .flatMap(Optional::stream)
                .filter(this::esActivo)
                .toList();
        if (perfiles.isEmpty()) {
            return new LimiteAutorizacion(usuarioId, ejercicioId, null, null,
                    BigDecimal.ZERO, false, false);
        }
        boolean ilimitado = perfiles.stream().anyMatch(perfil -> perfil.getMultiplico() == null);
        Integer multiplico = ilimitado ? null : perfiles.stream()
                .map(CompraAutoridadPerfil::getMultiplico)
                .max(Integer::compareTo)
                .orElse(null);
        BigDecimal referencia = compraReferenciaService.getByEjercicioId(ejercicioId)
                .map(CompraReferencia::getImporte)
                .orElse(null);
        BigDecimal limite = (!ilimitado && referencia != null && multiplico != null)
                ? referencia.multiply(BigDecimal.valueOf(multiplico))
                : null;
        return new LimiteAutorizacion(usuarioId, ejercicioId, multiplico, referencia, limite, ilimitado, true);
    }

    private boolean esActivo(CompraAutoridadPerfil perfil) {
        return perfil.getActivo() != null && perfil.getActivo() == 1;
    }

}
