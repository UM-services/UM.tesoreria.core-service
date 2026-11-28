package um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.web.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class ChequeraSerieRequest {

    private Long chequeraId;
    private Integer facultadId;
    private Integer tipoChequeraId;
    private Long chequeraSerieId;
    private BigDecimal personaId;
    private Integer documentoId;
    private Integer lectivoId;
    private Integer arancelTipoId;
    private Integer cursoId;
    private Byte asentado;
    private Integer geograficaId;
    private OffsetDateTime fecha;
    private Integer cuotasPagadas;
    private String observaciones;
    private Integer alternativaId;
    private Byte algoPagado;
    private Integer tipoImpresionId;
    private Byte flagPayperTic;
    private String usuarioId;
    private Byte enviado;
    private Byte retenida;
    private Long version;
    private Byte hpum;

    @DecimalMin(value = "0", inclusive = true)
    @DecimalMax(value = "1", inclusive = true)
    @Digits(integer = 1, fraction = 2)
    private BigDecimal becaPorcentaje;
    private String becaResolucion;
    private OffsetDateTime becaFecha;
    private Long becaUserId;

}
