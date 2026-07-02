package um.tesoreria.core.hexagonal.chequera.arancelTipo.infrastructure.persistence.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.infrastructure.persistence.entity.ArancelTipoEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaArancelTipoRepository extends JpaRepository<ArancelTipoEntity, Integer> {

    List<ArancelTipoEntity> findAllByHabilitado(Byte habilitado, Sort sort);
    Optional<ArancelTipoEntity> findByArancelTipoId(Integer arancelTipoId);
    Optional<ArancelTipoEntity> findTopByOrderByArancelTipoIdDesc();
    Optional<ArancelTipoEntity> findByArancelTipoIdCompleto(Integer arancelTipoIdCompleto);

    @Modifying
    void deleteByArancelTipoId(Integer arancelTipoId);

}
