package um.tesoreria.core.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.kotlin.model.TipoPago;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChequeraPagoDto {

    private Long chequeraPagoId;
    private Long chequeraCuotaId;
    private Integer facultadId;
    private Integer tipoChequeraId;
    private Long chequeraSerieId;
    private Integer productoId;
    private Integer alternativaId;
    private Integer cuotaId;
    private Integer orden;
    private Integer mes;
    private Integer anho;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    private OffsetDateTime fecha;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "UTC")
    private OffsetDateTime acreditacion;

    private BigDecimal importe;
    private String path;
    private String archivo;
    private String observaciones;
    private Long archivoBancoId;
    private Long archivoBancoIdAcreditacion;
    private Integer verificador;
    private Integer tipoPagoId;
    private String idMercadoPago;
    private TipoPago tipoPago;

}
