package um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.manifest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import um.tesoreria.core.hexagonal.usuarios.permiso.application.exception.PermisoManifestException;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;

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
 * Lee y valida `permisos.yaml` (inventario versionado).
 *
 * <p>El archivo es la fuente de verdad de las claves: cualquier error de formato
 * corta el arranque/sync con un mensaje que indica el ítem, para no aplicar un
 * inventario parcial.
 */
@Component
public class PermisoManifestLoader {

    /**
     * Convención `modulo.accion`. Se aceptan mayúsculas para no romper claves
     * legacy con camelCase (p. ej. `chequeras.cobranzas.pagoManual`).
     */
    private static final Pattern CLAVE =
            Pattern.compile("^[a-zA-Z0-9_]+(\\.[a-zA-Z0-9_]+)+$");
    private static final Pattern APLICACION = Pattern.compile("^[A-Z0-9_]+$");
    private static final Set<String> CAMPOS = Set.of(
            "aplicacion", "clave", "modulo", "descripcion", "activo");

    private final ResourceLoader resourceLoader;

    @Value("${app.permissions.manifest:classpath:permisos.yaml}")
    private String location;

    public PermisoManifestLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public List<Permiso> load() {
        Resource resource = resourceLoader.getResource(location);
        if (!resource.exists()) {
            throw new PermisoManifestException("No se encontró el manifiesto de permisos: " + location);
        }
        try (InputStream input = resource.getInputStream()) {
            return parse(input, location);
        } catch (IOException e) {
            throw new PermisoManifestException(
                    "No se pudo leer el manifiesto " + location + ": " + e.getMessage(), e);
        }
    }

    /**
     * Parser testeable sin Spring: recibe el contenido del manifiesto.
     */
    List<Permiso> parse(InputStream input, String origen) {
        Object rootObject;
        try {
            rootObject = new Yaml().load(input);
        } catch (YAMLException e) {
            throw new PermisoManifestException("YAML inválido en " + origen + ": " + e.getMessage(), e);
        }

        if (!(rootObject instanceof Map<?, ?> root)) {
            throw new PermisoManifestException(origen + " debe tener un objeto raíz");
        }
        for (Object key : root.keySet()) {
            if (!(key instanceof String campo) || !Set.of("version", "permisos").contains(campo)) {
                throw new PermisoManifestException(origen + " tiene un campo raíz desconocido: " + key);
            }
        }

        Object version = root.get("version");
        if (!(version instanceof Number numero) || numero.intValue() != 1) {
            throw new PermisoManifestException(origen + " debe declarar version: 1");
        }

        Object permisosObject = root.get("permisos");
        if (!(permisosObject instanceof List<?> lista)) {
            throw new PermisoManifestException(origen + " debe declarar una lista 'permisos'");
        }

        List<Permiso> resultado = new ArrayList<>();
        Set<String> vistas = new HashSet<>();
        for (int i = 0; i < lista.size(); i++) {
            if (!(lista.get(i) instanceof Map<?, ?> item)) {
                throw new PermisoManifestException(origen + ": el ítem " + i + " no es un objeto");
            }
            for (Object key : item.keySet()) {
                if (!(key instanceof String campo) || !CAMPOS.contains(campo)) {
                    throw new PermisoManifestException(
                            origen + ": el ítem " + i + " tiene un campo desconocido: " + key);
                }
            }

            String clave = texto(item.get("clave"), "clave", i, origen);
            if (!CLAVE.matcher(clave).matches() || clave.length() > 120) {
                throw new PermisoManifestException(origen + ": clave inválida en el ítem " + i
                        + " ('" + clave + "'); usá modulo.accion sin espacios");
            }

            String aplicacion = aplicacion(item.get("aplicacion"), i, origen);
            String modulo = texto(item.get("modulo"), "modulo", i, origen);
            if (modulo.length() > 60) {
                throw new PermisoManifestException(
                        origen + ": 'modulo' supera 60 caracteres en el ítem " + i);
            }
            String descripcion = texto(item.get("descripcion"), "descripcion", i, origen);
            if (descripcion.length() > 255) {
                throw new PermisoManifestException(
                        origen + ": 'descripcion' supera 255 caracteres en el ítem " + i);
            }

            if (!vistas.add(aplicacion + "::" + clave)) {
                throw new PermisoManifestException(
                        origen + ": clave duplicada (" + aplicacion + ", " + clave + ")");
            }

            resultado.add(Permiso.builder()
                    .aplicacion(aplicacion)
                    .clave(clave)
                    .modulo(modulo)
                    .descripcion(descripcion)
                    .activo((byte) (activo(item.get("activo"), i, origen) ? 1 : 0))
                    .build());
        }
        return resultado;
    }

    private String texto(Object value, String campo, int index, String origen) {
        if (!(value instanceof String texto) || texto.isBlank()) {
            throw new PermisoManifestException(
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
            throw new PermisoManifestException(
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
        throw new PermisoManifestException(
                origen + ": 'activo' inválido en el ítem " + index + " (usá true/false)");
    }
}
