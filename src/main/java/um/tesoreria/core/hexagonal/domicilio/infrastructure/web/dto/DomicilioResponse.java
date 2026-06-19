package um.tesoreria.core.hexagonal.domicilio.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomicilioResponse {
    private Long domicilioId;
    private BigDecimal personaId;
    private Integer documentoId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime fecha;
    private String calle;
    private String puerta;
    private String piso;
    private String dpto;
    private String telefono;
    private String movil;
    private String observaciones;
    private String codigoPostal;
    private Integer facultadId;
    private Integer provinciaId;
    private Integer localidadId;
    private String emailPersonal;
    private String emailInstitucional;
    private String laboral;
    private String emailPagador;
}
