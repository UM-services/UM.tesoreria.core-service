package um.tesoreria.core.hexagonal.personas.legajo.infrastructure.web.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class LegajoRequest {

    private BigDecimal personaId;
    private Integer documentoId;
    private Integer facultadId;
    private Long numeroLegajo;
    private OffsetDateTime fecha;
    private Integer lectivoId;
    private Integer planId;
    private Integer carreraId;
    private Byte tieneCarrera;
    private Integer geograficaId;
    private String contrasenha;
    private Byte intercambio;
}