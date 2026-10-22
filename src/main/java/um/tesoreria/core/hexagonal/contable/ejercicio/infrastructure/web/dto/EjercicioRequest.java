package um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.web.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class EjercicioRequest {
    private Integer ejercicioId;
    private String nombre;
    private OffsetDateTime fechaInicio;
    private OffsetDateTime fechaFinal;
    private Byte bloqueado;
    private Integer ordenContableResultado;
    private Integer ordenContableBienesUso;
}
