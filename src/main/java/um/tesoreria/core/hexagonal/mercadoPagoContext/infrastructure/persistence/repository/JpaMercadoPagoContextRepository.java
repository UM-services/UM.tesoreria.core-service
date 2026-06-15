package um.tesoreria.core.hexagonal.mercadoPagoContext.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import um.tesoreria.core.hexagonal.mercadoPagoContext.infrastructure.persistence.entity.MercadoPagoContextEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface JpaMercadoPagoContextRepository extends JpaRepository<MercadoPagoContextEntity, Long> {

    List<MercadoPagoContextEntity> findAllByChequeraCuotaIdAndActivo(Long chequeraCuotaId, Byte activo);

    List<MercadoPagoContextEntity> findAllByStatusAndChequeraPagoIdIsNull(String status);

    Optional<MercadoPagoContextEntity> findByChequeraCuotaIdAndActivo(Long chequeraCuotaId, Byte activo);

    Optional<MercadoPagoContextEntity> findByMercadoPagoContextId(Long mercadoPagoContextId);

    List<MercadoPagoContextEntity> findAllByActivoOrderByMercadoPagoContextIdDesc(Byte activo);

    List<MercadoPagoContextEntity> findAllByActivoAndFechaVencimientoBetween(Byte activo, OffsetDateTime desde, OffsetDateTime hasta);

    List<MercadoPagoContextEntity> findAllByChequeraCuotaIdInAndActivo(List<Long> chequeraCuotaIds, Byte activo);

}
