package um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Pago a imputar sobre una chequera reemplazada ({@code chequera_pago_reemplazo}).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReemplazoPago {

    private Long clave;
    private Integer facultadId;
    private Integer tipoChequeraId;
    private Long chequeraSerieId;
    private Integer productoId;
    private Integer alternativaId;
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

}
