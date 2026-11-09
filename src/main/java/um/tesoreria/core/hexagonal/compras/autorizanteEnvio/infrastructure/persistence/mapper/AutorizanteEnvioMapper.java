package um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.domain.model.AutorizanteEnvio;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.persistence.entity.AutorizanteEnvioDependenciaEntity;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AutorizanteEnvioMapper {

    public AutorizanteEnvio toDomain(Integer usuarioId, List<AutorizanteEnvioDependenciaEntity> entities) {
        if (entities == null) {
            return new AutorizanteEnvio(usuarioId, Set.of());
        }
        Set<Integer> dependenciaIds = entities.stream()
                .map(AutorizanteEnvioDependenciaEntity::getDependenciaId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return new AutorizanteEnvio(usuarioId, dependenciaIds);
    }

    public AutorizanteEnvioDependenciaEntity toEntity(Integer usuarioId, Integer dependenciaId) {
        return new AutorizanteEnvioDependenciaEntity(usuarioId, dependenciaId);
    }

}
