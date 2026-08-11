package um.tesoreria.core.hexagonal.dependencias.geografica.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeograficaResponse {
    private Integer geograficaId;
    private String nombre;
    private Byte sinChequera;
}
