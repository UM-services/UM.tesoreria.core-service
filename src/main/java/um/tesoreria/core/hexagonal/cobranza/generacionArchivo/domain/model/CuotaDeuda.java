package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Cuota con deuda lista para facturar. Es la vista mínima de {@code vw_chequera_cuota_deuda}
 * (o de {@code vw_cuota_deuda_ppt} para PayPerTIC).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuotaDeuda {

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
    private OffsetDateTime vencimiento1;
    private BigDecimal importe1;
    private OffsetDateTime vencimiento2;
    private BigDecimal importe2;
    private OffsetDateTime vencimiento3;
    private BigDecimal importe3;
    private String codigoBarras;
    private Byte pagado;
    private Byte baja;
    private String facultadNombre;
    private String tipoChequeraNombre;
    private String geograficaNombre;
    private String personaNombre;
    private String productoNombre;
    private String lectivoNombre;

}
