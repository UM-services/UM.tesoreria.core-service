package um.tesoreria.core.hexagonal.umhub.campanha.infrastructure.web.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampanhaResponse {
    private UUID campanhaId;
    private String nombre;
    private Byte activa;
}
