package um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.infrastructure.persistence.entity.GuaraniPropuestaTipoChequeraEntity;

@Repository
public interface JpaGuaraniPropuestaTipoChequeraRepository
        extends JpaRepository<GuaraniPropuestaTipoChequeraEntity, Integer> {
    Optional<GuaraniPropuestaTipoChequeraEntity> findByPropuestaGuaraniAndLectivoId(
            Integer propuestaGuarani, Integer lectivoId);
}
