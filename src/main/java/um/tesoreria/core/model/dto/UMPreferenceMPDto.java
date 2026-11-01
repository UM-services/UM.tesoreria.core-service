package um.tesoreria.core.model.dto;

import lombok.*;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.model.ReservaVacante;
import um.tesoreria.core.util.Jsonifier;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UMPreferenceMPDto {

    private MercadoPagoContext mercadoPagoContext;
    private ChequeraCuota chequeraCuota;
    private ReservaVacante reservaVacante;

    /**
     * Solo se completa en el endpoint de renovaciones: vencimiento/importe que debe publicarse.
     */
    private OffsetDateTime fechaVencimientoDestino;
    private BigDecimal importeDestino;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }
}
