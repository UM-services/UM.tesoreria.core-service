package um.tesoreria.core.hexagonal.mercadoPagoContext.infrastructure.persistence.repository;

import um.tesoreria.core.hexagonal.mercadoPagoContext.infrastructure.persistence.entity.MercadoPagoContextEntity;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Consultas dinamicas del slice de contextos de Mercado Pago.
 *
 * <p>Se implementan con Criteria API (no se usa {@code @Query} en el proyecto).</p>
 */
public interface JpaMercadoPagoContextRepositoryCustom {

    /**
     * Contextos activos de cuotas pendientes que tienen un vencimiento posterior al guardado,
     * dentro de la ventana indicada (o marcados con {@code changed = 1} para reintento).
     *
     * @param desde          limite inferior inclusivo sobre {@code fechaVencimiento}
     * @param hastaExclusivo limite superior exclusivo sobre {@code fechaVencimiento}
     * @param reintentos     true = solo contextos changed=1 anteriores a la ventana
     * @param afterFecha     cursor keyset: fecha del ultimo item procesado, puede ser null
     * @param afterId        cursor keyset: id del ultimo item procesado, puede ser null
     * @param size           tamano maximo de la pagina
     */
    List<MercadoPagoContextEntity> findRenovaciones(OffsetDateTime desde,
                                                    OffsetDateTime hastaExclusivo,
                                                    boolean reintentos,
                                                    OffsetDateTime afterFecha,
                                                    Long afterId,
                                                    int size);

    /**
     * Identificadores de cuota que tienen mas de un contexto activo.
     */
    List<Long> findChequeraCuotaIdsWithMultipleActiveContexts();

}
