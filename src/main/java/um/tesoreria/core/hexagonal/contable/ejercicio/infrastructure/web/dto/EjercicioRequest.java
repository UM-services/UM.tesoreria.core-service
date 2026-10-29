package um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.web.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class EjercicioRequest {
    private Integer ejercicioId;
    private String nombre;
    private OffsetDateTime fechaInicio;
    private OffsetDateTime fechaFinal;
    // BigDecimal y no Byte/Integer: Jackson trunca 0.9 a 0 y un PUT con bloqueado=0.9 reabriría un ejercicio
    // bloqueado; el mapper rechaza todo lo que no sea un entero exacto.
    private BigDecimal bloqueado;
    private BigDecimal ordenContableResultado;
    private BigDecimal ordenContableBienesUso;
}
