package um.tesoreria.core.hexagonal.usuarios.permiso.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.usuarios.permiso.application.exception.PermisoException;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.SyncPermisosResultado;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.in.SyncPermisosUseCase;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.out.PermisoRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Reconciliación idempotente del catálogo contra el manifiesto versionado.
 *
 * <p>No borra filas: las claves que desaparecen del manifiesto quedan con
 * {@code activo = 0}. Eso conserva las asignaciones (`rol_permiso`,
 * `usuario_permiso`) y permite reactivarlas si la clave vuelve.
 */
@Component
@RequiredArgsConstructor
public class SyncPermisosUseCaseImpl implements SyncPermisosUseCase {

    private final PermisoRepository repository;

    @Override
    @Transactional
    public SyncPermisosResultado sync(List<Permiso> manifiesto, boolean dryRun) {
        if (manifiesto == null || manifiesto.isEmpty()) {
            throw new PermisoException(
                    "El manifiesto de permisos está vacío: se rechaza para no retirar todo el catálogo");
        }

        Map<String, Permiso> existentes = new LinkedHashMap<>();
        for (Permiso permiso : repository.findAll()) {
            existentes.put(clave(permiso.getAplicacion(), permiso.getClave()), permiso);
        }

        Set<String> declaradas = new HashSet<>();
        List<String> agregadas = new ArrayList<>();
        List<String> actualizadas = new ArrayList<>();
        int sinCambios = 0;

        for (Permiso item : manifiesto) {
            normalizar(item);
            String key = clave(item.getAplicacion(), item.getClave());
            if (!declaradas.add(key)) {
                throw new PermisoException("Clave duplicada en el manifiesto: " + key);
            }

            Permiso actual = existentes.get(key);
            if (actual == null) {
                if (!dryRun) {
                    repository.save(item);
                }
                agregadas.add(key);
                continue;
            }

            boolean cambio = false;
            if (!Objects.equals(actual.getDescripcion(), item.getDescripcion())) {
                actual.setDescripcion(item.getDescripcion());
                cambio = true;
            }
            if (!Objects.equals(actual.getModulo(), item.getModulo())) {
                actual.setModulo(item.getModulo());
                cambio = true;
            }
            if (!Objects.equals(actual.getActivo(), item.getActivo())) {
                actual.setActivo(item.getActivo());
                cambio = true;
            }

            if (cambio) {
                if (!dryRun) {
                    repository.save(actual);
                }
                actualizadas.add(key);
            } else {
                sinCambios++;
            }
        }

        List<String> retiradas = new ArrayList<>();
        for (Map.Entry<String, Permiso> entry : existentes.entrySet()) {
            if (declaradas.contains(entry.getKey())) {
                continue;
            }
            Permiso actual = entry.getValue();
            if (actual.getActivo() != null && actual.getActivo() == 1) {
                actual.setActivo((byte) 0);
                if (!dryRun) {
                    repository.save(actual);
                }
                retiradas.add(entry.getKey());
            }
        }

        Collections.sort(agregadas);
        Collections.sort(actualizadas);
        Collections.sort(retiradas);

        return SyncPermisosResultado.builder()
                .dryRun(dryRun)
                .totalManifiesto(manifiesto.size())
                .agregados(agregadas.size())
                .actualizados(actualizadas.size())
                .retirados(retiradas.size())
                .sinCambios(sinCambios)
                .clavesAgregadas(agregadas)
                .clavesActualizadas(actualizadas)
                .clavesRetiradas(retiradas)
                .build();
    }

    private void normalizar(Permiso permiso) {
        if (permiso == null) {
            throw new PermisoException("El manifiesto contiene un permiso nulo");
        }
        if (permiso.getAplicacion() == null || permiso.getAplicacion().isBlank()) {
            permiso.setAplicacion("TESORERIA");
        } else {
            permiso.setAplicacion(permiso.getAplicacion().trim().toUpperCase(Locale.ROOT));
        }
        if (permiso.getClave() == null || permiso.getClave().isBlank()) {
            throw new PermisoException("El manifiesto contiene una clave vacía");
        }
        permiso.setClave(permiso.getClave().trim());
        if (permiso.getModulo() == null || permiso.getModulo().isBlank()) {
            throw new PermisoException("La clave " + permiso.getClave() + " no declara módulo");
        }
        permiso.setModulo(permiso.getModulo().trim());
        if (permiso.getDescripcion() == null || permiso.getDescripcion().isBlank()) {
            throw new PermisoException("La clave " + permiso.getClave() + " no declara descripción");
        }
        permiso.setDescripcion(permiso.getDescripcion().trim());
        if (permiso.getActivo() == null) {
            permiso.setActivo((byte) 1);
        }
    }

    private String clave(String aplicacion, String clave) {
        String app = aplicacion == null || aplicacion.isBlank()
                ? "TESORERIA"
                : aplicacion.trim().toUpperCase(Locale.ROOT);
        return app + "::" + clave;
    }
}
