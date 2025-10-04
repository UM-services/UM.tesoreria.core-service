package um.tesoreria.core.hexagonal.cursoCargoContratado.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

    private Integer categoriaId;
    private String nombre;
    private BigDecimal basico;
    private Byte docente;
    private Byte noDocente;
    private Byte liquidaPorHora;
    private BigDecimal estadoDocente;

}
