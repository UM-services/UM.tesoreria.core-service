package um.tesoreria.core.model.dto;

import lombok.*;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.chequeraCuota.infrastructure.persistence.entity.ChequeraCuotaEntity;
import um.tesoreria.core.util.Jsonifier;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UMPreferenceMPDto {

    private MercadoPagoContext mercadoPagoContext;
    private ChequeraCuotaEntity chequeraCuota;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }
}
