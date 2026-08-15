package um.tesoreria.core.hexagonal.personas.legajo.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import um.tesoreria.core.kotlin.model.Carrera;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LegajoResponse {
    private Long legajoId;
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
    private Carrera carrera;
}