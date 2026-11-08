package um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.SyncPermisosResultado;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.in.SyncPermisosUseCase;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.manifest.PermisoManifestLoader;

/**
 * Aplica `permisos.yaml` al arrancar el servicio.
 *
 * <p>Es lo que mantiene alineados todos los entornos con el inventario
 * versionado. Se puede apagar con {@code app.permissions.manifest-sync.enabled=false}
 * y simular con {@code ...dry-run=true}. Si el manifiesto es inválido, el
 * arranque falla: es preferible a dejar el catálogo desincronizado en silencio.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PermisoManifestSyncRunner implements ApplicationRunner {

    private final PermisoManifestLoader manifestLoader;
    private final SyncPermisosUseCase syncPermisosUseCase;

    @Value("${app.permissions.manifest-sync.enabled:true}")
    private boolean enabled;

    @Value("${app.permissions.manifest-sync.dry-run:false}")
    private boolean dryRun;

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) {
            log.info("Sincronización del manifiesto de permisos deshabilitada "
                    + "(app.permissions.manifest-sync.enabled=false)");
            return;
        }

        try {
            SyncPermisosResultado resultado = syncPermisosUseCase.sync(
                    manifestLoader.load(), dryRun);
            log.info("Manifiesto de permisos{}: total={} agregados={} actualizados={} "
                            + "retirados={} sinCambios={}",
                    resultado.isDryRun() ? " (dry-run)" : "",
                    resultado.getTotalManifiesto(),
                    resultado.getAgregados(),
                    resultado.getActualizados(),
                    resultado.getRetirados(),
                    resultado.getSinCambios());
            if (!resultado.getClavesAgregadas().isEmpty()) {
                log.info("Permisos agregados: {}", resultado.getClavesAgregadas());
            }
            if (!resultado.getClavesActualizadas().isEmpty()) {
                log.info("Permisos actualizados: {}", resultado.getClavesActualizadas());
            }
            if (!resultado.getClavesRetiradas().isEmpty()) {
                log.warn("Permisos retirados (activo=0): {}", resultado.getClavesRetiradas());
            }
        } catch (RuntimeException e) {
            log.error("No se pudo sincronizar el manifiesto de permisos", e);
            throw e;
        }
    }
}
