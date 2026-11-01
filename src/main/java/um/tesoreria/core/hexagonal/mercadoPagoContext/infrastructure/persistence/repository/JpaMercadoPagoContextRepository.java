package um.tesoreria.core.hexagonal.mercadoPagoContext.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import um.tesoreria.core.hexagonal.mercadoPagoContext.infrastructure.persistence.entity.MercadoPagoContextEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaMercadoPagoContextRepository extends JpaRepository<MercadoPagoContextEntity, Long>,
        JpaMercadoPagoContextRepositoryCustom {

    List<MercadoPagoContextEntity> findAllByChequeraCuotaIdAndActivo(Long chequeraCuotaId, Byte activo);

    List<MercadoPagoContextEntity> findAllByStatusAndChequeraPagoIdIsNull(String status);

    Optional<MercadoPagoContextEntity> findByChequeraCuotaIdAndActivo(Long chequeraCuotaId, Byte activo);

    Optional<MercadoPagoContextEntity> findByReservaVacanteId(UUID reservaVacanteId);

    Optional<MercadoPagoContextEntity> findByMercadoPagoContextId(Long mercadoPagoContextId);

    List<MercadoPagoContextEntity> findAllByActivoAndFechaVencimientoBetween(Byte activo, OffsetDateTime desde, OffsetDateTime hasta);

    List<MercadoPagoContextEntity> findAllByChequeraCuotaIdInAndActivo(List<Long> chequeraCuotaIds, Byte activo);

}
