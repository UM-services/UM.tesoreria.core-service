package um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.manifest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import um.tesoreria.core.hexagonal.usuarios.rol.application.exception.RolManifestException;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.RolSemilla;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Lee y valida `roles.yaml` (semilla versionada de roles y su matriz de permisos).
 *
 * <p>Igual que el manifiesto de permisos: cualquier error de formato corta el
 * arranque/sync con un mensaje que indica el ítem, para no aplicar una semilla
 * parcial en silencio.
 */
@Component
public class RolManifestLoader {

    /** Convención del plan: MAYÚSCULAS, dígitos y '_'. */
    private static final Pattern NOMBRE = Pattern.compile("^[A-Z0-9_]{1,80}$");
    private static final Pattern APLICACION = Pattern.compile("^[A-Z0-9_]+$");
    /**
     * Clave exacta (`chequeras.eliminar`), comodín de módulo (`chequeras.*`)
     * o comodín total (`*`).
     */
    private static final Pattern PERMISO =
            Pattern.compile("^(\\*|[a-zA-Z0-9_]+(\\.[a-zA-Z0-9_]+)*(\\.\\*)?)$");
    private static final Set<String> CAMPOS = Set.of(
            "nombre", "descripcion", "aplicacion", "activo", "permisos");

    private final ResourceLoader resourceLoader;

    @Value("${app.roles.manifest:classpath:roles.yaml}")
    private String location;

    public RolManifestLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public List<RolSemilla> load() {
        Resource resource = resourceLoader.getResource(location);
        if (!resource.exists()) {
            throw new RolManifestException("No se encontró el manifiesto de roles: " + location);
        }
        try (InputStream input = resource.getInputStream()) {
            return parse(input, location);
        } catch (IOException e) {
            throw new RolManifestException(
                    "No se pudo leer el manifiesto " + location + ": " + e.getMessage(), e);
        }
    }

    /**
     * Parser testeable sin Spring: recibe el contenido del manifiesto.
     */
    List<RolSemilla> parse(InputStream input, String origen) {
        Object rootObject;
        try {
            rootObject = new Yaml().load(input);
        } catch (YAMLException e) {
            throw new RolManifestException("YAML inválido en " + origen + ": " + e.getMessage(), e);
        }

        if (!(rootObject instanceof Map<?, ?> root)) {
            throw new RolManifestException(origen + " debe tener un objeto raíz");
        }
        for (Object key : root.keySet()) {
            if (!(key instanceof String campo) || !Set.of("version", "roles").contains(campo)) {
                throw new RolManifestException(origen + " tiene un campo raíz desconocido: " + key);
            }
        }

        Object version = root.get("version");
        if (!(version instanceof Number numero) || numero.intValue() != 1) {
            throw new RolManifestException(origen + " debe declarar version: 1");
        }

        Object rolesObject = root.get("roles");
        if (!(rolesObject instanceof List<?> lista)) {
            throw new RolManifestException(origen + " debe declarar una lista 'roles'");
        }

        List<RolSemilla> resultado = new ArrayList<>();
        Set<String> vistos = new HashSet<>();
        for (int i = 0; i < lista.size(); i++) {
            if (!(lista.get(i) instanceof Map<?, ?> item)) {
                throw new RolManifestException(origen + ": el ítem " + i + " no es un objeto");
            }
            for (Object key : item.keySet()) {
                if (!(key instanceof String campo) || !CAMPOS.contains(campo)) {
                    throw new RolManifestException(
                            origen + ": el ítem " + i + " tiene un campo desconocido: " + key);
                }
            }

            String nombre = texto(item.get("nombre"), "nombre", i, origen).toUpperCase(Locale.ROOT);
            if (!NOMBRE.matcher(nombre).matches()) {
                throw new RolManifestException(origen + ": nombre inválido en el ítem " + i
                        + " ('" + nombre + "'); usá MAYÚSCULAS, dígitos y '_'");
            }

            String aplicacion = aplicacion(item.get("aplicacion"), i, origen);
            if (!vistos.add(aplicacion + "::" + nombre)) {
                throw new RolManifestException(
                        origen + ": rol duplicado (" + aplicacion + ", " + nombre + ")");
            }

            String descripcion = texto(item.get("descripcion"), "descripcion", i, origen);
            if (descripcion.length() > 255) {
                throw new RolManifestException(
                        origen + ": 'descripcion' supera 255 caracteres en el ítem " + i);
            }

            List<String> permisos = new ArrayList<>();
            Object permisosObject = item.get("permisos");
            if (permisosObject != null) {
                if (!(permisosObject instanceof List<?> patrones)) {
                    throw new RolManifestException(
                            origen + ": 'permisos' debe ser una lista en el ítem " + i);
                }
                for (Object patronObject : patrones) {
                    if (!(patronObject instanceof String patron) || patron.isBlank()) {
                        throw new RolManifestException(
                                origen + ": permiso vacío en el ítem " + i);
                    }
                    String limpio = patron.trim();
                    if (!PERMISO.matcher(limpio).matches() || limpio.length() > 120) {
                        throw new RolManifestException(origen + ": permiso inválido en el ítem " + i
                                + " ('" + limpio + "'); usá modulo.accion, modulo.* o *");
                    }
                    if (!permisos.contains(limpio)) {
                        permisos.add(limpio);
                    }
                }
            }

            resultado.add(RolSemilla.builder()
                    .nombre(nombre)
                    .descripcion(descripcion)
                    .aplicacion(aplicacion)
                    .activo((byte) (activo(item.get("activo"), i, origen) ? 1 : 0))
                    .permisos(permisos)
                    .build());
        }
        return resultado;
    }

    private String texto(Object value, String campo, int index, String origen) {
        if (!(value instanceof String texto) || texto.isBlank()) {
            throw new RolManifestException(
                    origen + ": falta '" + campo + "' en el ítem " + index);
        }
        return texto.trim();
    }

    private String aplicacion(Object value, int index, String origen) {
        if (value == null) {
            return "TESORERIA";
        }
        String aplicacion = texto(value, "aplicacion", index, origen).toUpperCase(Locale.ROOT);
        if (!APLICACION.matcher(aplicacion).matches() || aplicacion.length() > 20) {
            throw new RolManifestException(
                    origen + ": 'aplicacion' inválida en el ítem " + index + " ('" + aplicacion + "')");
        }
        return aplicacion;
    }

    private boolean activo(Object value, int index, String origen) {
        if (value == null) {
            return true;
        }
        if (value instanceof Boolean booleano) {
            return booleano;
        }
        if (value instanceof Number numero) {
            return numero.intValue() == 1;
        }
        if (value instanceof String texto) {
            String normalizado = texto.trim().toLowerCase(Locale.ROOT);
            if (normalizado.equals("true") || normalizado.equals("1")) {
                return true;
            }
            if (normalizado.equals("false") || normalizado.equals("0")) {
                return false;
            }
        }
        throw new RolManifestException(
                origen + ": 'activo' inválido en el ítem " + index + " (usá true/false)");
    }
}
