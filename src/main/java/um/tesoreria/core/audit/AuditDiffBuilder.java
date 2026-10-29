package um.tesoreria.core.audit;

import lombok.RequiredArgsConstructor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.Type;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.sql.Blob;
import java.sql.Clob;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Compara el estado anterior y el actual de una entidad y arma el diff.
 *
 * <p>Sólo propiedades escalares: las asociaciones y colecciones se ignoran para no
 * serializar medio grafo. Los campos sensibles se enmascaran.</p>
 */
@Component
@RequiredArgsConstructor
public class AuditDiffBuilder {

    public enum Operation {
        INSERT,
        UPDATE,
        DELETE
    }

    public record DiffResult(List<String> changedFields,
                             Map<String, Object> before,
                             Map<String, Object> after,
                             boolean truncated) {
    }

    private static final List<String> IGNORED = List.of("created", "updated");

    private final AuditProperties properties;
    private final ObjectMapper objectMapper;

    public DiffResult build(EntityPersister persister, Object[] beforeState, Object[] afterState,
                            Operation operation) {
        String entityName = simpleName(persister.getEntityName());
        String[] names = persister.getPropertyNames();
        Type[] types = persister.getPropertyTypes();

        List<String> changed = new ArrayList<>();
        Map<String, Object> before = new LinkedHashMap<>();
        Map<String, Object> after = new LinkedHashMap<>();

        for (int index = 0; index < names.length; index++) {
            String name = names[index];
            if (IGNORED.contains(name) || types[index].isAssociationType()) {
                continue;
            }

            Object oldValue = beforeState != null && index < beforeState.length ? beforeState[index] : null;
            Object newValue = afterState != null && index < afterState.length ? afterState[index] : null;

            if (operation == Operation.UPDATE && Objects.deepEquals(oldValue, newValue)) {
                continue;
            }
            if (operation != Operation.DELETE && newValue == null) {
                continue;
            }
            if (operation == Operation.DELETE && oldValue == null) {
                continue;
            }

            changed.add(name);
            if (oldValue != null) {
                before.put(name, mask(entityName, name, oldValue));
            }
            if (newValue != null) {
                after.put(name, mask(entityName, name, newValue));
            }
        }

        boolean truncated = tooLarge(before, after);
        if (truncated) {
            before = new LinkedHashMap<>();
            after = new LinkedHashMap<>();
        }

        return new DiffResult(changed, before, after, truncated);
    }

    /**
     * Enmascara según las reglas de {@code tesoreria.audit.mask-fields}, que aceptan
     * {@code campo} (en cualquier entidad) o {@code Entidad.campo} (sólo en esa entidad).
     *
     * <p>El enmascaramiento por entidad es lo que permite auditar
     * {@code PermisoEntity.clave} (la clave funcional del permiso) sin ocultarla por
     * llamarse igual que la contraseña de {@code UsuarioEntity}.</p>
     */
    Object mask(String entity, String field, Object value) {
        if (!isSensitive(entity, field)) {
            return normalize(value);
        }
        if ("password".equalsIgnoreCase(field) || "clave".equalsIgnoreCase(field)
                || "token".equalsIgnoreCase(field)) {
            return "****";
        }
        Object normalized = normalize(value);
        if (normalized == null) {
            return null;
        }
        String text = normalized.toString();
        return text.length() <= 4 ? "****" : "****" + text.substring(text.length() - 4);
    }

    boolean isSensitive(String entity, String field) {
        return properties.getMaskFields().stream().anyMatch(rule -> applies(rule, entity, field));
    }

    private boolean applies(String rule, String entity, String field) {
        if (rule == null || rule.isBlank()) {
            return false;
        }
        int dot = rule.indexOf('.');
        if (dot < 0) {
            return rule.trim().equalsIgnoreCase(field);
        }
        String ruleEntity = rule.substring(0, dot);
        String ruleField = rule.substring(dot + 1);
        return ruleField.trim().equalsIgnoreCase(field)
                && normalizeEntity(ruleEntity).equals(normalizeEntity(entity));
    }

    /**
     * Normaliza el nombre de entidad para comparar reglas: ignora mayúsculas y el sufijo
     * {@code Entity}, así {@code Usuario.clave}, {@code UsuarioEntity.clave} y
     * {@code usuarioEntity.clave} son la misma regla.
     */
    private String normalizeEntity(String name) {
        if (name == null) {
            return "";
        }
        String trimmed = name.trim();
        if (trimmed.endsWith("Entity")) {
            trimmed = trimmed.substring(0, trimmed.length() - "Entity".length());
        }
        return trimmed.toLowerCase(Locale.ROOT);
    }

    private String simpleName(String entityName) {
        int index = entityName.lastIndexOf('.');
        return index < 0 ? entityName : entityName.substring(index + 1);
    }

    private Object normalize(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof TemporalAccessor) {
            return value.toString();
        }
        if (value instanceof byte[] bytes) {
            return "<binary:" + bytes.length + ">";
        }
        if (value instanceof Blob) {
            return "<blob>";
        }
        if (value instanceof Clob) {
            return "<clob>";
        }
        if (value instanceof Enum<?> enumeration) {
            return enumeration.name();
        }
        if (value instanceof Collection<?> collection) {
            return "<collection:" + collection.size() + ">";
        }
        if (value instanceof Map<?, ?> map) {
            return "<map:" + map.size() + ">";
        }
        return value;
    }

    private boolean tooLarge(Map<String, Object> before, Map<String, Object> after) {
        try {
            return objectMapper.writeValueAsString(Map.of("before", before, "after", after))
                    .length() > properties.getMaxPayloadChars();
        } catch (RuntimeException e) {
            return true;
        }
    }
}
