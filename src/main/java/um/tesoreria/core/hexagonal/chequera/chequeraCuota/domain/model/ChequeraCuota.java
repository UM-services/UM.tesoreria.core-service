package um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import um.tesoreria.core.hexagonal.chequera.producto.domain.model.Producto;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.util.Jsonifier;
import um.tesoreria.core.util.Jsonifyable;

import java.math.BigDecimal;
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

}
