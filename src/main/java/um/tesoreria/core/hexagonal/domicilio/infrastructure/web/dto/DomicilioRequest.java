package um.tesoreria.core.hexagonal.domicilio.infrastructure.web.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class DomicilioRequest {
    private Long domicilioId;
    private BigDecimal personaId;
    private Integer documentoId;
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
}
