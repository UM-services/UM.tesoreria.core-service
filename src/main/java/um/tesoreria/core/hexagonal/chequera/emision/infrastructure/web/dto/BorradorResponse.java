package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorradorResponse {

    private Long borradorId;
    private String estado;
    private BigDecimal personaId;
    private Integer documentoId;
    private Integer facultadId;
    private String facultadNombre;
    private String personaApellido;
    private String personaNombre;
    private Integer lectivoId;
    private String lectivoNombre;
    private Integer tipoChequeraId;
    private String tipoChequeraNombre;
    private Integer geograficaId;
    private String geograficaNombre;
    private Integer arancelTipoId;
    private Integer alternativaId;
    private Integer cursoId;
    private Long chequeraSerieId;
    private OffsetDateTime fecha;
    private Byte hpum;
    private BigDecimal becaPorcentaje;
    private String becaResolucion;
    private OffsetDateTime becaFecha;
    private Long becaUserId;
    private boolean calculaBeca;
    private String emailPersonal;
    private String emailInstitucional;
    private String emailCopia;
    private boolean incluyeBarras;
    private boolean copiaFacultad;
    private Long chequeraId;
    private Long chequeraSerieIdEmitida;
    private String auditoriaId;
    private String usuarioId;
    private Integer version;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private OffsetDateTime emitidoAt;
    private BigDecimal totalBorrador;
    private List<CuotaDto> cuotas;
    private List<AlternativaDto> alternativas;
    private List<ArancelTipoDto> arancelTipos;
    private List<AvisoDto> avisos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CuotaDto {
        private Long borradorCuotaId;
        private Long chequeraCuotaId;
        private Integer productoId;
        private Integer alternativaId;
        private Integer cuotaId;
        private Integer mes;
        private Integer anio;
        private OffsetDateTime vencimiento1;
        private OffsetDateTime vencimiento2;
        private OffsetDateTime vencimiento3;
        private BigDecimal importe1Original;
        private BigDecimal importe2Original;
        private BigDecimal importe3Original;
        private BigDecimal importe1Borrador;
        private BigDecimal importe2Borrador;
        private BigDecimal importe3Borrador;
        private Integer arancelTipoId;
        private Integer tramoId;
        private BigDecimal descuentoPagoTermino;
        private Byte pagado;
        private Byte baja;
        private Byte manual;
        private Byte compensada;
        private Byte modificado;
        private Byte bloqueado;
        private boolean editable;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlternativaDto {
        private Integer alternativaId;
        private Integer productoId;
        private String titulo;
        private Integer cuotas;
        private Integer descuentoPagoTermino;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArancelTipoDto {
        private Integer arancelTipoId;
        private String nombre;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvisoDto {
        private String codigo;
        private String mensaje;
        private boolean bloqueante;
    }
}
