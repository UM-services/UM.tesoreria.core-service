package um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.out;

import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.PreferenciaPatch;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MercadoPagoContextRepository {

    List<MercadoPagoContext> findAllByChequeraCuotaIdAndActivo(Long chequeraCuotaId, Byte activo);

    List<MercadoPagoContext> findAllByChequeraCuotaIdInAndActivo(List<Long> chequeraCuotaIds, Byte activo);

    List<MercadoPagoContext> findAllByActivoAndFechaVencimientoBetween(Byte activo, OffsetDateTime desde, OffsetDateTime hasta);

    List<MercadoPagoContext> findRenovaciones(OffsetDateTime desde, OffsetDateTime hastaExclusivo, boolean reintentos, OffsetDateTime afterFecha, Long afterId, int size);

    List<Long> findChequeraCuotaIdsWithMultipleActiveContexts();

    Optional<MercadoPagoContext> findByMercadoPagoContextId(Long mercadoPagoContextId);

    Optional<MercadoPagoContext> findByChequeraCuotaIdAndActivo(Long chequeraCuotaId, Byte activo);

    Optional<MercadoPagoContext> findByReservaVacanteIdAndActivo(UUID reservaVacanteId, Byte activo);

    List<MercadoPagoContext> findAllByStatusAndChequeraPagoIdIsNull(String status);

    MercadoPagoContext save(MercadoPagoContext context);

    List<MercadoPagoContext> saveAll(List<MercadoPagoContext> contexts);

    /**
     * Actualiza solo los campos vinculados a la preferencia de Mercado Pago, sin tocar
     * datos de pago (status, importe pagado, fechas de pago, etc.).
     */
    Optional<MercadoPagoContext> updatePreferencia(PreferenciaPatch datos, Long mercadoPagoContextId);

}
