package um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import um.tesoreria.core.hexagonal.chequera.producto.domain.model.Producto;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.util.Jsonifyable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChequeraCuota implements Jsonifyable {

    private Long chequeraCuotaId;
    private Long chequeraId;
    private Integer facultadId;
    private Integer tipoChequeraId;
    private Long chequeraSerieId;
    private Integer productoId;
    private Integer alternativaId;
    private Integer cuotaId;
    private Integer mes;
    private Integer anho;
    private Integer arancelTipoId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime vencimiento1;

    private BigDecimal importe1;
    private BigDecimal importe1Original;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime vencimiento2;

    private BigDecimal importe2;
    private BigDecimal importe2Original;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime vencimiento3;

    private BigDecimal importe3;
    private BigDecimal importe3Original;
    private String codigoBarras;
    private String i2Of5;
    private Byte pagado;
    private Byte baja;
    private Byte manual;
    private Byte compensada;
    private Integer tramoId;

    private Facultad facultad;
    private TipoChequera tipoChequera;
    private Producto producto;
    private ChequeraSerie chequeraSerie;

    public String cuotaKey() {
        return this.facultadId + "." + this.tipoChequeraId + "." + this.chequeraSerieId + "." + this.productoId + "."
                + this.alternativaId + "." + this.cuotaId;
    }

    /**
     * Sanea los importes aplicando el porcentaje de arancel sobre el importe original:
     * {@code importeN = importe{N}Original * porcentaje / 100} (escala 4, HALF_UP).
     * No modifica {@code importe{N}Original} ni los vencimientos.
     */
    public void aplicarPorcentajeArancel(BigDecimal porcentaje) {
        if (porcentaje == null) {
            return;
        }
        if (importe1Original != null) {
            this.importe1 = calcularImporteArancel(importe1Original, porcentaje);
        }
        if (importe2Original != null) {
            this.importe2 = calcularImporteArancel(importe2Original, porcentaje);
        }
        if (importe3Original != null) {
            this.importe3 = calcularImporteArancel(importe3Original, porcentaje);
        }
    }

    private static BigDecimal calcularImporteArancel(BigDecimal importeOriginal, BigDecimal porcentaje) {
        return importeOriginal.multiply(porcentaje).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
    }

}
