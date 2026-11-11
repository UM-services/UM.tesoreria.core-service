package um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Agregado que representa a un usuario habilitado para aprobar o rechazar el envío de pedidos
 * de compra sobre un conjunto de dependencias.
 *
 * <p>El conjunto es el concepto de negocio: un autorizante puede cubrir varias dependencias.
 * La tabla puente es un detalle de persistencia.</p>
 */
public record AutorizanteEnvio(
        Integer usuarioId,
        Set<Integer> dependenciaIds) {

    public AutorizanteEnvio {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El autorizante requiere usuario");
        }
        dependenciaIds = dependenciaIds == null
                ? Set.of()
                : Collections.unmodifiableSet(new LinkedHashSet<>(dependenciaIds));
    }

    public List<Integer> dependenciaIdsOrdenadas() {
        return dependenciaIds.stream().sorted().toList();
    }

    public boolean cubre(Integer dependenciaId) {
        return dependenciaId != null && dependenciaIds.contains(dependenciaId);
    }

    public AutorizanteEnvio agregar(Integer dependenciaId) {
        if (dependenciaId == null) {
            throw new IllegalArgumentException("La dependencia es obligatoria");
        }
        if (dependenciaIds.contains(dependenciaId)) {
            return this;
        }
        Set<Integer> ids = new LinkedHashSet<>(dependenciaIds);
        ids.add(dependenciaId);
        return new AutorizanteEnvio(usuarioId, ids);
    }

    public AutorizanteEnvio quitar(Integer dependenciaId) {
        if (dependenciaId == null || !dependenciaIds.contains(dependenciaId)) {
            return this;
        }
        Set<Integer> ids = new LinkedHashSet<>(dependenciaIds);
        ids.remove(dependenciaId);
        return new AutorizanteEnvio(usuarioId, ids);
    }

    public AutorizanteEnvio reemplazar(Collection<Integer> ids) {
        return new AutorizanteEnvio(usuarioId, ids == null ? Set.of() : new LinkedHashSet<>(ids));
    }
}
