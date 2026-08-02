package um.tesoreria.core.hexagonal.persona.infrastructure.persistence.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.hexagonal.persona.infrastructure.persistence.entity.PersonaKeyEntity;

@Repository
public interface JpaPersonaKeyRepository extends JpaRepository<PersonaKeyEntity, String>, PersonaKeyRepositoryCustom {

    List<PersonaKeyEntity> findAllByUnifiedIn(List<String> keys, Sort sort);
}
