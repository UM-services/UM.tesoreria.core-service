package um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Fila de {@code archivo_banco}: una línea del archivo de rendición, con las claves de chequera
 * ya normalizadas.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchivoBanco {

    private Long id;
    private String codigoEmpresa;
    private Integer identificacion;
    private Integer productoId;
    private Integer mesPago;
    private Integer anhoPago;
    private Integer cuotaId;
    private BigDecimal importe;
    private OffsetDateTime fechaPago;
    private String marcaAcreditacion;
    private OffsetDateTime fechaAcreditacion;
    private String verificador;
    private String path;
    private String archivo;
    private OffsetDateTime cuando;
    private Byte pasado;
    private Integer tipoPagoId;
    private Byte repetido;

}
