package um.tesoreria.core.hexagonal.usuarios.rol.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.in.GetAllPermisosUseCase;
import um.tesoreria.core.hexagonal.usuarios.rol.application.exception.RolManifestException;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.RolSemilla;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.SyncRolesResultado;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.in.SyncRolesUseCase;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.out.RolRepository;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.model.RolPermiso;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.ports.out.RolPermisoRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Reconciliación idempotente de los roles semilla contra `roles.yaml`.
 *
 * <p>Alcance: sólo los roles declarados en el manifiesto. Los roles creados desde
 * la UI no se tocan. Para cada rol declarado se reconcilia también su matriz
 * `rol_permiso` (agrega faltantes, quita sobrantes): el manifiesto es la fuente de
 * verdad de la composición de esos roles, y las excepciones por usuario van en
 * `usuario_permiso`.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SyncRolesUseCaseImpl implements SyncRolesUseCase {

    private final RolRepository rolRepository;
    private final RolPermisoRepository rolPermisoRepository;
    // Excepción cross-slice autorizada: lee el catálogo del slice usuarios.permiso
    // por su puerto de entrada (mismo criterio que rolPermiso).
    private final GetAllPermisosUseCase getAllPermisosUseCase;

    @Override
    @Transactional
    public SyncRolesResultado sync(List<RolSemilla> manifiesto, boolean dryRun) {
        if (manifiesto == null || manifiesto.isEmpty()) {
            throw new RolManifestException(
                    "El manifiesto de roles está vacío: se rechaza para no dejar el sistema sin roles semilla");
        }

        Map<String, Permiso> catalogo = new LinkedHashMap<>();
        Map<Long, String> clavesPorId = new LinkedHashMap<>();
        for (Permiso permiso : getAllPermisosUseCase.getAllPermisos()) {
            String etiqueta = clave(aplicacion(permiso), permiso.getClave());
            catalogo.put(etiqueta, permiso);
            clavesPorId.put(permiso.getPermisoId(), etiqueta);
        }

        List<String> rolesCreados = new ArrayList<>();
        List<String> rolesModificados = new ArrayList<>();
        List<String> asignacionesAgregadas = new ArrayList<>();
        List<String> asignacionesQuitadas = new ArrayList<>();
        int sinCambios = 0;

        for (RolSemilla semilla : manifiesto) {
            normalizar(semilla);
            String etiqueta = clave(semilla.getAplicacion(), semilla.getNombre());
            Set<Long> declaradas = resolverPermisos(semilla, catalogo);

            Rol actual = rolRepository
                    .findByAplicacionAndNombre(semilla.getAplicacion(), semilla.getNombre())
                    .orElse(null);

            if (actual == null) {
                rolesCreados.add(etiqueta);
                if (dryRun) {
                    for (Long permisoId : declaradas) {
                        asignacionesAgregadas.add(etiqueta + " -> " + clavesPorId.get(permisoId));
                    }
                    continue;
                }
                Rol creado = rolRepository.save(Rol.builder()
                        .nombre(semilla.getNombre())
                        .descripcion(semilla.getDescripcion())
                        .aplicacion(semilla.getAplicacion())
                        .activo(semilla.getActivo())
                        .build());
                for (Long permisoId : declaradas) {
                    rolPermisoRepository.save(RolPermiso.builder()
                            .rolId(creado.getRolId())
                            .permisoId(permisoId)
                            .build());
                    asignacionesAgregadas.add(etiqueta + " -> " + clavesPorId.get(permisoId));
                }
                continue;
            }

            boolean cambio = false;
            if (!Objects.equals(actual.getDescripcion(), semilla.getDescripcion())) {
                actual.setDescripcion(semilla.getDescripcion());
                cambio = true;
            }
            if (!Objects.equals(actual.getActivo(), semilla.getActivo())) {
                actual.setActivo(semilla.getActivo());
                cambio = true;
            }
            if (cambio) {
                if (!dryRun) {
                    rolRepository.save(actual);
                }
                rolesModificados.add(etiqueta);
            } else {
                sinCambios++;
            }

            Set<Long> actuales = new LinkedHashSet<>();
            for (RolPermiso asignacion : rolPermisoRepository.findAllByRolId(actual.getRolId())) {
                actuales.add(asignacion.getPermisoId());
            }

            for (Long permisoId : declaradas) {
                if (actuales.contains(permisoId)) {
                    continue;
                }
                if (!dryRun) {
                    rolPermisoRepository.save(RolPermiso.builder()
                            .rolId(actual.getRolId())
                            .permisoId(permisoId)
                            .build());
                }
                asignacionesAgregadas.add(etiqueta + " -> " + clavesPorId.get(permisoId));
            }
            for (Long permisoId : actuales) {
                if (declaradas.contains(permisoId)) {
                    continue;
                }
                if (!dryRun) {
                    rolPermisoRepository.deleteByRolIdAndPermisoId(actual.getRolId(), permisoId);
                }
                asignacionesQuitadas.add(etiqueta + " -> " + claveDe(permisoId, clavesPorId));
            }
        }

        Collections.sort(rolesCreados);
        Collections.sort(rolesModificados);
        Collections.sort(asignacionesAgregadas);
        Collections.sort(asignacionesQuitadas);

        return SyncRolesResultado.builder()
                .dryRun(dryRun)
                .totalManifiesto(manifiesto.size())
                .rolesAgregados(rolesCreados.size())
                .rolesActualizados(rolesModificados.size())
                .rolesSinCambios(sinCambios)
                .permisosAgregados(asignacionesAgregadas.size())
                .permisosQuitados(asignacionesQuitadas.size())
                .rolesCreados(rolesCreados)
                .rolesModificados(rolesModificados)
                .asignacionesAgregadas(asignacionesAgregadas)
                .asignacionesQuitadas(asignacionesQuitadas)
                .build();
    }

    /**
     * Resuelve los patrones declarados a ids del catálogo. Una clave exacta
     * inexistente corta el arranque; un comodín sin coincidencias sólo avisa.
     */
    private Set<Long> resolverPermisos(RolSemilla semilla, Map<String, Permiso> catalogo) {
        Set<Long> ids = new LinkedHashSet<>();
        for (String patron : semilla.getPermisos()) {
            Set<Long> coincidencias = new LinkedHashSet<>();
            if (patron.equals("*")) {
                for (Permiso permiso : catalogo.values()) {
                    if (semilla.getAplicacion().equals(aplicacion(permiso))) {
                        coincidencias.add(permiso.getPermisoId());
                    }
                }
            } else if (patron.endsWith(".*")) {
                String prefijo = patron.substring(0, patron.length() - 1);
                for (Permiso permiso : catalogo.values()) {
                    if (semilla.getAplicacion().equals(aplicacion(permiso))
                            && permiso.getClave() != null
                            && permiso.getClave().startsWith(prefijo)) {
                        coincidencias.add(permiso.getPermisoId());
                    }
                }
            } else {
                Permiso permiso = catalogo.get(clave(semilla.getAplicacion(), patron));
                if (permiso == null) {
                    throw new RolManifestException("El rol " + semilla.getNombre()
                            + " referencia un permiso inexistente: " + patron);
                }
                coincidencias.add(permiso.getPermisoId());
            }
            if (coincidencias.isEmpty()) {
                log.warn("El patrón '{}' del rol {} no coincide con ningún permiso del catálogo",
                        patron, semilla.getNombre());
            }
            ids.addAll(coincidencias);
        }
        return ids;
    }

    private void normalizar(RolSemilla semilla) {
        if (semilla == null) {
            throw new RolManifestException("El manifiesto contiene un rol nulo");
        }
        if (semilla.getNombre() == null || semilla.getNombre().isBlank()) {
            throw new RolManifestException("El manifiesto contiene un rol sin nombre");
        }
        semilla.setNombre(semilla.getNombre().trim());
        if (semilla.getAplicacion() == null || semilla.getAplicacion().isBlank()) {
            semilla.setAplicacion("TESORERIA");
        } else {
            semilla.setAplicacion(semilla.getAplicacion().trim().toUpperCase(Locale.ROOT));
        }
        if (semilla.getDescripcion() == null || semilla.getDescripcion().isBlank()) {
            throw new RolManifestException("El rol " + semilla.getNombre() + " no declara descripción");
        }
        semilla.setDescripcion(semilla.getDescripcion().trim());
        if (semilla.getActivo() == null) {
            semilla.setActivo((byte) 1);
        }
        List<String> permisos = new ArrayList<>();
        if (semilla.getPermisos() != null) {
            for (String patron : semilla.getPermisos()) {
                if (patron == null || patron.isBlank()) {
                    throw new RolManifestException(
                            "El rol " + semilla.getNombre() + " declara un permiso vacío");
                }
                String limpio = patron.trim();
                if (!permisos.contains(limpio)) {
                    permisos.add(limpio);
                }
            }
        }
        semilla.setPermisos(permisos);
    }

    private String aplicacion(Permiso permiso) {
        return permiso.getAplicacion() == null || permiso.getAplicacion().isBlank()
                ? "TESORERIA"
                : permiso.getAplicacion().trim().toUpperCase(Locale.ROOT);
    }

    private String clave(String aplicacion, String clave) {
        String app = aplicacion == null || aplicacion.isBlank()
                ? "TESORERIA"
                : aplicacion.trim().toUpperCase(Locale.ROOT);
        return app + "::" + clave;
    }

    private String claveDe(Long permisoId, Map<Long, String> clavesPorId) {
        String etiqueta = clavesPorId.get(permisoId);
        return etiqueta != null ? etiqueta : String.valueOf(permisoId);
    }
}
