package um.tesoreria.core.hexagonal.chequera.chequeraPago.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChequeraPagoRequest {

    @NotNull
    private Integer facultadId;

    @NotNull
    private Integer tipoChequeraId;

    @NotNull
    private Long chequeraSerieId;

    @NotNull
    private Integer productoId;

    @NotNull
    private Integer alternativaId;

    @NotNull
    private Integer cuotaId;

    private Integer orden;
    private Integer mes;
    private Integer anho;
    private OffsetDateTime fecha;
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

}
