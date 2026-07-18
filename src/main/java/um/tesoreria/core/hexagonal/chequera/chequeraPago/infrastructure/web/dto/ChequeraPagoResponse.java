package um.tesoreria.core.hexagonal.chequera.chequeraPago.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static lombok.Builder.Default;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChequeraPagoResponse {

    private Long chequeraPagoId;
    private Long chequeraCuotaId;
    private Integer facultadId;
    private Integer tipoChequeraId;
    private Long chequeraSerieId;
    private Integer productoId;
    private Integer alternativaId;
    private Integer cuotaId;
    private Integer orden;

    @Default
    private Integer mes = 0;

    @Default
    private Integer anho = 0;

    private OffsetDateTime fecha;
    private OffsetDateTime acreditacion;

    @Default
    private BigDecimal importe = BigDecimal.ZERO;

    @Default
    private String path = "";

    @Default
    private String archivo = "";

    @Default
    private String observaciones = "";

    private Long archivoBancoId;
    private Long archivoBancoIdAcreditacion;

    @Default
    private Integer verificador = 0;
    private Integer tipoPagoId;
    private String idMercadoPago;

}
