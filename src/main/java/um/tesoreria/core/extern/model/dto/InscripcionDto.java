package um.tesoreria.core.extern.model.dto;

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
public class InscripcionDto {

    private Integer facultadId;
    private BigDecimal personaId;
    private Integer documentoId;
    private Integer lectivoId;
    private Long inscripcionId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    private OffsetDateTime fecha;

    @Builder.Default
    private String chequera = "";
    @Builder.Default
    private String matricula = "";
    @Builder.Default
    private Long factura = 0L;
    @Builder.Default
    private Integer curso = 0;
    private Integer planId;
    private Integer carreraId;
    private Integer geograficaId;
    @Builder.Default
    private Byte asentado = 0;
    @Builder.Default
    private Byte provisoria = 0;
    @Builder.Default
    private Integer cohorte = 0;
    @Builder.Default
    private Byte remota = 0;
    @Builder.Default
    private Byte imprimir = 0;
    @Builder.Default
    private Integer edad = 0;
    @Builder.Default
    private String observaciones = "";
    @Builder.Default
    private Integer offsetpago = 0;
    @Builder.Default
    private Integer libre = 0;
    private Integer divisionId;
    @Builder.Default
    private Byte debematricula = 0;

}
