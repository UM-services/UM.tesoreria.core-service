package um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Línea leída de un archivo de rendición, ya normalizada a las claves de chequera.
 *
 * <p>Los parsers completan la parte "del archivo" (claves, importe, fechas). El servicio de
 * importación completa la parte "resuelta" (mes, año, vencimiento, importe de la cuota, reemplazo
 * y estado).</p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LineaImportacion {

    private Integer numeroLinea;
    private String referencia;
    /** Identificador de la fila de {@code paypertic} cuando el canal es PayPerTIC. */
    private String payperticId;

    private Integer facultadId;
    private Integer tipoChequeraId;
    private Long chequeraSerieId;
    private Integer productoId;
    private Integer alternativaId;
    private Integer cuotaId;

    private Integer mes;
    private Integer anho;

    private BigDecimal importe;
    private OffsetDateTime fechaPago;
    private OffsetDateTime fechaAcreditacion;
    private String marcaAcreditacion;
    private String verificador;

    private Integer tipoPagoId;
    private BigDecimal importeCuota;

    /** Débito Directo: {@code refUnivocaDeb}; VISA: se completa con el débito encontrado. */
    private Long debitoId;
    private String numeroCupon;
    private String codigoTipoRegistro;
    private boolean rechazado;
    private String motivoRechazo;

    private boolean reemplazo;
    private EstadoLinea estado;
    private String motivo;

    private String facultadNombre;
    private String tipoChequeraNombre;
    private String geograficaNombre;
    private String personaNombre;
    private String productoNombre;
    private OffsetDateTime vencimiento;

}
