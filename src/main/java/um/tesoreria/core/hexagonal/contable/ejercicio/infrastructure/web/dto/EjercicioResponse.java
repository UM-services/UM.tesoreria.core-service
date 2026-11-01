package um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class EjercicioResponse {
    private Integer ejercicioId;
    private String nombre;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime fechaInicio;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime fechaFinal;
    private Byte bloqueado;
    private Integer ordenContableResultado;
    private Integer ordenContableBienesUso;
}
