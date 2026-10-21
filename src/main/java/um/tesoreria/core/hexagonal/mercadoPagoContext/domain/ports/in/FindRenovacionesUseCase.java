package um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in;

import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.RenovacionMercadoPago;

import java.time.OffsetDateTime;
import java.util.List;

public interface FindRenovacionesUseCase {

    /**
     * Candidatos a renovar el enlace de pago.
     *
     * @param hoy          fecha de referencia (00:00 del dia en curso)
     * @param lookbackDias dias hacia atras sobre el vencimiento publicado
     * @param reintentos   true = solo contextos changed=1 fuera de la ventana
     * @param afterFecha   cursor keyset (fecha del ultimo item)
     * @param afterId      cursor keyset (id del ultimo item)
     * @param size         tamano de pagina
     */
    List<RenovacionMercadoPago> findRenovaciones(OffsetDateTime hoy, int lookbackDias, boolean reintentos,
                                                OffsetDateTime afterFecha, Long afterId, int size);

}
