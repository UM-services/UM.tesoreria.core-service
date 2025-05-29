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

    private String chequera = "";
    private String matricula = "";
    private Long factura = 0L;
    private Integer curso = 0;
    private Integer planId;
    private Integer carreraId;
    private Integer geograficaId;
    private Byte asentado = 0;
    private Byte provisoria = 0;
    private Integer cohorte = 0;
    private Byte remota = 0;
    private Byte imprimir = 0;
    private Integer edad = 0;
    private String observaciones = "";
    private Integer offsetpago = 0;
    private Integer libre = 0;
    private Integer divisionId;
    private Byte debematricula = 0;

}
