package um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.persistence.entity.AutorizanteEnvioDependenciaEntity;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.persistence.entity.AutorizanteEnvioDependenciaId;

import java.util.List;

@Repository
public interface JpaAutorizanteEnvioDependenciaRepository
        extends JpaRepository<AutorizanteEnvioDependenciaEntity, AutorizanteEnvioDependenciaId> {

    List<AutorizanteEnvioDependenciaEntity> findAllByUsuarioIdOrderByDependenciaIdAsc(Integer usuarioId);

    List<AutorizanteEnvioDependenciaEntity> findAllByOrderByUsuarioIdAscDependenciaIdAsc();

    boolean existsByUsuarioIdAndDependenciaId(Integer usuarioId, Integer dependenciaId);

    void deleteByUsuarioIdAndDependenciaId(Integer usuarioId, Integer dependenciaId);

}
