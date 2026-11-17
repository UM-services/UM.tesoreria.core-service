package um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.runner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.SyncRolesResultado;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.in.SyncRolesUseCase;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.manifest.RolManifestLoader;

/**
 * Aplica `roles.yaml` al arrancar el servicio, después del manifiesto de permisos
 * (que es el que crea las claves de `permiso`).
 *
 * <p>Se puede apagar con {@code app.roles.manifest-sync.enabled=false} y simular
 * con {@code ...dry-run=true}. Si el manifiesto es inválido, el arranque falla:
 * es preferible a dejar los roles semilla desincronizados en silencio.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class RolesManifestSyncRunner implements ApplicationRunner {

    private final RolManifestLoader manifestLoader;
    private final SyncRolesUseCase syncRolesUseCase;

    @Value("${app.roles.manifest-sync.enabled:true}")
    private boolean enabled;

    @Value("${app.roles.manifest-sync.dry-run:false}")
    private boolean dryRun;

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) {
            log.info("Sincronización del manifiesto de roles deshabilitada "
                    + "(app.roles.manifest-sync.enabled=false)");
            return;
        }

        try {
            SyncRolesResultado resultado = syncRolesUseCase.sync(
                    manifestLoader.load(), dryRun);
            log.info("Manifiesto de roles{}: total={} creados={} actualizados={} "
                            + "sinCambios={} asignacionesAgregadas={} asignacionesQuitadas={}",
                    resultado.isDryRun() ? " (dry-run)" : "",
                    resultado.getTotalManifiesto(),
                    resultado.getRolesAgregados(),
                    resultado.getRolesActualizados(),
                    resultado.getRolesSinCambios(),
                    resultado.getPermisosAgregados(),
                    resultado.getPermisosQuitados());
            if (!resultado.getRolesCreados().isEmpty()) {
                log.info("Roles creados: {}", resultado.getRolesCreados());
            }
            if (!resultado.getRolesModificados().isEmpty()) {
                log.info("Roles actualizados: {}", resultado.getRolesModificados());
            }
            if (!resultado.getAsignacionesAgregadas().isEmpty()) {
                log.info("Asignaciones agregadas: {}", resultado.getAsignacionesAgregadas());
            }
            if (!resultado.getAsignacionesQuitadas().isEmpty()) {
                log.warn("Asignaciones quitadas por no estar en el manifiesto: {}",
                        resultado.getAsignacionesQuitadas());
            }
        } catch (RuntimeException e) {
            log.error("No se pudo sincronizar el manifiesto de roles", e);
            throw e;
        }
    }
}
