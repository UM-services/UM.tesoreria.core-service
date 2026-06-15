package um.tesoreria.core.hexagonal.umhub.campanha.domain.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Campanha {
    private UUID campanhaId;
    private String nombre;
    private Byte activa;
    private BigDecimal valorReserva;
    private LocalDateTime created;
}
