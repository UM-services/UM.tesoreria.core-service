package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Snapshot para imprimir un borrador. Es lo único que necesita {@code tesoreria-sender} para
 * renderizar el documento con la leyenda BORRADOR: no hay número de chequera ni código de barras
 * porque todavía no existen.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorradorImpresion {

    private Long borradorId;
    private String estado;
    private BigDecimal personaId;
    private Integer documentoId;
    private String apellido;
    private String nombre;
    private Integer facultadId;
    private String facultadNombre;
    private Integer tipoChequeraId;
    private String tipoChequeraNombre;
    private Integer lectivoId;
    private String lectivoNombre;
    private Integer arancelTipoId;
    private String arancelTipoDescripcion;
    private Integer alternativaId;
    private String alternativaTitulo;
    private BigDecimal becaPorcentaje;
    private Byte hpum;
    private OffsetDateTime fecha;
    private String carrera;
    private BigDecimal total;
    private List<Cuota> cuotas;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Cuota {
        private Integer productoId;
        private String productoNombre;
        private Integer alternativaId;
        private Integer cuotaId;
        private Integer mes;
        private Integer anio;
        private OffsetDateTime vencimiento1;
        private OffsetDateTime vencimiento2;
        private OffsetDateTime vencimiento3;
        private BigDecimal importe1;
        private BigDecimal importe2;
        private BigDecimal importe3;
        private Byte pagado;
        private Byte baja;
        private Byte manual;
        private Byte compensada;
    }
}
