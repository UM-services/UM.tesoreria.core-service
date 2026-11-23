package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.persistence.entity.IngresoItemEntity;

import java.time.LocalDate;
import java.util.List;

/** Lectura de {@code ingreso_items} por fecha de acreditación y tipo de pago. */
public interface JpaIngresoItemRepository
        extends JpaRepository<IngresoItemEntity, IngresoItemEntity.IngresoItemId> {

    List<IngresoItemEntity> findAllByAcreditacionAndTipoPagoIdOrderByDebitaDescNumeroCuentaAsc(
            LocalDate acreditacion, Integer tipoPagoId);

}
