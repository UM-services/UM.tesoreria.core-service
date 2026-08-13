package um.tesoreria.core.hexagonal.personas.legajo.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import um.tesoreria.core.kotlin.model.Carrera;
import um.tesoreria.core.util.Jsonifyable;

/**
 * Modelo de dominio para Legajo.
 * Sin dependencias de framework (JPA, Spring, etc.)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Legajo implements Jsonifyable {

    private Long legajoId;
    private BigDecimal personaId;
    private Integer documentoId;
    private Integer facultadId;

    @Builder.Default
    private Long numeroLegajo = 0L;

    private OffsetDateTime fecha;
    private Integer lectivoId;
    private Integer planId;
    private Integer carreraId;

    @Builder.Default
    private Byte tieneCarrera = 0;

    private Integer geograficaId;
    private String contrasenha;

    @Builder.Default
    private Byte intercambio = 0;

    private Carrera carrera;
}