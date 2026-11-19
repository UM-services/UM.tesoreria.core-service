package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Datos para crear un borrador de emisión.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrearBorradorData {

    private BigDecimal personaId;
    private Integer documentoId;
    private Integer facultadId;
    private Integer lectivoId;
    private Integer tipoChequeraId;
    private Integer arancelTipoId;
    private Integer alternativaId;
    private Integer cursoId;
    private Long chequeraSerieId;
    private BigDecimal becaPorcentaje;
    private String becaResolucion;
    private OffsetDateTime becaFecha;
    private Long becaUserId;
    private Byte hpum;
    private Boolean calculaBeca;
    private OffsetDateTime fecha;
    private FuenteCompletado fuente;
    private String emailPersonal;
    private String emailInstitucional;
    private Boolean incluyeBarras;
    private Boolean copiaFacultad;
}
