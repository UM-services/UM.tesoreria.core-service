package um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in;

import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.PreferenciaPatch;

public interface UpdatePreferenciaContextUseCase {

    /**
     * Actualiza solo los datos de la preferencia de Mercado Pago de un contexto.
     * No modifica informacion de pagos.
     */
    MercadoPagoContext updatePreferencia(PreferenciaPatch datos, Long mercadoPagoContextId);

}
