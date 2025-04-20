package um.tesoreria.core.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.kotlin.model.Producto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChequeraCuotaPagosDto {

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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    private OffsetDateTime vencimiento1;
    private BigDecimal importe1;
    private BigDecimal importe1Original;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    private OffsetDateTime vencimiento2;
    private BigDecimal importe2;
    private BigDecimal importe2Original;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
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
    private Producto producto;

    private List<ChequeraPagoDto> chequeraPagos;

}
