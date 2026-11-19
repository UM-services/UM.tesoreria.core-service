package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Cuota dentro de un borrador de emisión.
 *
 * <p>{@code importeNOriginal} es el importe del diseño (lectivo_cuota) y
 * {@code importeNBorrador} el importe que se va a emitir (ya con beca, descuento por
 * pago en término y porcentaje de arancel aplicados).</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorradorCuota {

    private Long borradorCuotaId;
    private Long chequeraCuotaId;
    private Integer productoId;
    private Integer alternativaId;
    private Integer cuotaId;
    private Integer mes;
    private Integer anio;
    private OffsetDateTime vencimiento1;
    private OffsetDateTime vencimiento2;
    private OffsetDateTime vencimiento3;
    private BigDecimal importe1Original;
    private BigDecimal importe2Original;
    private BigDecimal importe3Original;
    private BigDecimal importe1Borrador;
    private BigDecimal importe2Borrador;
    private BigDecimal importe3Borrador;
    private Integer arancelTipoId;
    private Integer tramoId;
    private BigDecimal descuentoPagoTermino;
    private Byte pagado;
    private Byte baja;
    private Byte manual;
    private Byte compensada;
    private Byte modificado;
    private Byte bloqueado;

    public String key() {
        return productoId + "." + alternativaId + "." + cuotaId;
    }

    public boolean bloqueada() {
        return esVerdadero(manual) || esVerdadero(compensada) || esVerdadero(pagado) || esVerdadero(baja);
    }

    /**
     * Cuota inmutable: viene de la chequera real y no se puede editar desde el borrador.
     *
     * <p>Replica {@code frmImpChequera} (línea 1231): una cuota pagada, dada de baja,
     * compensada o marcada como manual en la chequera original queda bloqueada. El
     * {@code manual} que el propio wizard agrega al editar no bloquea: el candado de
     * origen se persiste en {@code bloqueado}.</p>
     */
    public boolean inmutable() {
        return esVerdadero(bloqueado) || esVerdadero(pagado) || esVerdadero(baja) || esVerdadero(compensada);
    }

    public static boolean esVerdadero(Byte valor) {
        return valor != null && valor != 0;
    }
}
