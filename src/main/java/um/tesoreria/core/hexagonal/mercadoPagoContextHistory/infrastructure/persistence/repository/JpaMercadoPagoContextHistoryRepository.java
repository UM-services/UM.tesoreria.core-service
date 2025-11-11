package um.tesoreria.core.hexagonal.mercadoPagoContextHistory.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import um.tesoreria.core.hexagonal.mercadoPagoContextHistory.infrastructure.persistence.entity.MercadoPagoContextHistoryEntity;

import java.util.UUID;

public interface JpaMercadoPagoContextHistoryRepository extends JpaRepository<MercadoPagoContextHistoryEntity, UUID> {

}
