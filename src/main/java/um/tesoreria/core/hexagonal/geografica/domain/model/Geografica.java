package um.tesoreria.core.hexagonal.geografica.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Geografica {
    private Integer geograficaId;
    private String nombre;
    private Byte sinChequera;
}
