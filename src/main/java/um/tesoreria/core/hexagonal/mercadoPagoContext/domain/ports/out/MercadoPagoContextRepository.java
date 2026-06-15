package um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.out;

import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface MercadoPagoContextRepository {

    List<MercadoPagoContext> findAllByChequeraCuotaIdAndActivo(Long chequeraCuotaId, Byte activo);

    List<MercadoPagoContext> findAllByChequeraCuotaIdInAndActivo(List<Long> chequeraCuotaIds, Byte activo);

    List<MercadoPagoContext> findAllByActivoAndFechaVencimientoBetween(Byte activo, OffsetDateTime desde, OffsetDateTime hasta);

    Optional<MercadoPagoContext> findByMercadoPagoContextId(Long mercadoPagoContextId);

    Optional<MercadoPagoContext> findByChequeraCuotaIdAndActivo(Long chequeraCuotaId, Byte activo);

    List<MercadoPagoContext> findAllByActivoOrderByMercadoPagoContextIdDesc(Byte activo);

    List<MercadoPagoContext> findAllByStatusAndChequeraPagoIdIsNull(String status);

    MercadoPagoContext save(MercadoPagoContext context);

    List<MercadoPagoContext> saveAll(List<MercadoPagoContext> contexts);

}
