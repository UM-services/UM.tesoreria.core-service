package um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.infrastructure.persistence.entity.GuaraniBeneficioEntity;

import java.util.Optional;
import java.util.List;

@Repository
public interface JpaGuaraniBeneficioRepository extends JpaRepository<GuaraniBeneficioEntity, Integer> {
    Optional<GuaraniBeneficioEntity> findByRequisito(Integer requisito);
    List<GuaraniBeneficioEntity> findByRequisitoIn(List<Integer> requisitos);
}
