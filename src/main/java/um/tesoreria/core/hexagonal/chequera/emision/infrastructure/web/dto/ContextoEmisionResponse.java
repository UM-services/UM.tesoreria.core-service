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
public class ContextoEmisionResponse {

    private Integer lectivoId;
    private PersonaDto persona;
    private DomicilioDto domicilio;
    private DeudaDto deuda;
    private PermisosDto permisos;
    private List<SerieDto> series;
    private List<AvisoDto> avisos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonaDto {
        private BigDecimal personaId;
        private Integer documentoId;
        private String apellido;
        private String nombre;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DomicilioDto {
        private Long domicilioId;
        private String emailPersonal;
        private String emailInstitucional;
        private String emailPagador;
        private boolean emailPersonalValido;
        private boolean emailInstitucionalValido;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeudaDto {
        private Integer cuotas;
        private BigDecimal importe;
        private Integer cuotasPermitidas;
        private boolean excedida;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermisosDto {
        private boolean puedeEmitir;
        private boolean puedeImprimir;
        private boolean puedeModificar;
        private boolean puedeForzarDeuda;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SerieDto {
        private Long chequeraId;
        private Integer facultadId;
        private String facultadNombre;
        private Integer tipoChequeraId;
        private String tipoChequeraNombre;
        private Long chequeraSerieId;
        private Integer claseChequeraId;
        private Integer geograficaId;
        private String geograficaNombre;
        private Integer arancelTipoId;
        private String arancelTipoDescripcion;
        private Integer alternativaId;
        private Integer cursoId;
        private OffsetDateTime fecha;
        private OffsetDateTime ultimoEnvio;
        private Byte enviado;
        private Byte retenida;
        private boolean pagada;
        private boolean deBaja;
        private OffsetDateTime bajaFecha;
        private String bajaObservaciones;
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
