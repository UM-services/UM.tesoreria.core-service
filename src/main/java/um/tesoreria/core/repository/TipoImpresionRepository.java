package um.tesoreria.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.TipoImpresion;

@Repository
public interface TipoImpresionRepository extends JpaRepository<TipoImpresion, Integer> {

    Optional<TipoImpresion> findByTipoImpresionId(Integer tipoImpresionId);

}