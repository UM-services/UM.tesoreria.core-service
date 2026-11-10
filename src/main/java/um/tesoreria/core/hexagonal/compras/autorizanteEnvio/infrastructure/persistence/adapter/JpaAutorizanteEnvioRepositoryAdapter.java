package um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.persistence.adapter;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.model.AutorizanteEnvio;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.ports.out.AutorizanteEnvioRepository;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.persistence.entity.AutorizanteEnvioDependenciaEntity;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.persistence.mapper.AutorizanteEnvioMapper;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.persistence.repository.JpaAutorizanteEnvioDependenciaRepository;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Persistencia JPA de la tabla puente autorizante de envío ↔ dependencia.
 *
 * <p>El dominio trabaja con el agregado {@link AutorizanteEnvio}; este adaptador traduce el
 * conjunto a filas y aplica el diff de altas y bajas en una sola transacción.</p>
 */
@Component
@RequiredArgsConstructor
public class JpaAutorizanteEnvioRepositoryAdapter implements AutorizanteEnvioRepository {

    private final JpaAutorizanteEnvioDependenciaRepository repository;
    private final AutorizanteEnvioMapper mapper;

    @Override
    public Optional<AutorizanteEnvio> findByUsuarioId(Integer usuarioId) {
        List<AutorizanteEnvioDependenciaEntity> entities =
                repository.findAllByUsuarioIdOrderByDependenciaIdAsc(usuarioId);
        if (entities.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(mapper.toDomain(usuarioId, entities));
    }

    @Override
    public List<AutorizanteEnvio> findAll() {
        Map<Integer, Set<Integer>> byUsuario = new LinkedHashMap<>();
        repository.findAllByOrderByUsuarioIdAscDependenciaIdAsc().forEach(entity ->
                byUsuario.computeIfAbsent(entity.getUsuarioId(), key -> new LinkedHashSet<>())
                        .add(entity.getDependenciaId()));
        return byUsuario.entrySet().stream()
                .map(entry -> new AutorizanteEnvio(entry.getKey(), entry.getValue()))
                .toList();
    }

    @Override
    @Transactional
    public AutorizanteEnvio save(AutorizanteEnvio autorizante) {
        Integer usuarioId = autorizante.usuarioId();
        Set<Integer> actuales = repository.findAllByUsuarioIdOrderByDependenciaIdAsc(usuarioId).stream()
                .map(AutorizanteEnvioDependenciaEntity::getDependenciaId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Set<Integer> objetivo = autorizante.dependenciaIds();

        List<Integer> aEliminar = actuales.stream()
                .filter(dependenciaId -> !objetivo.contains(dependenciaId))
                .toList();
        List<AutorizanteEnvioDependenciaEntity> aAgregar = objetivo.stream()
                .filter(dependenciaId -> !actuales.contains(dependenciaId))
                .map(dependenciaId -> mapper.toEntity(usuarioId, dependenciaId))
                .toList();

        aEliminar.forEach(dependenciaId ->
                repository.deleteByUsuarioIdAndDependenciaId(usuarioId, dependenciaId));
        if (!aAgregar.isEmpty()) {
            repository.saveAll(aAgregar);
        }
        return autorizante;
    }

    @Override
    public boolean existe(Integer usuarioId, Integer dependenciaId) {
        return repository.existsByUsuarioIdAndDependenciaId(usuarioId, dependenciaId);
    }

}
