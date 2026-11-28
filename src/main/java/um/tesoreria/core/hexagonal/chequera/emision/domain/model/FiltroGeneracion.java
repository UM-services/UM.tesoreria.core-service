package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Filtros del job de generación masiva de borradores.
 *
 * <p>El arancel y la alternativa no se piden: se heredan de cada serie. La clase de chequera y el
 * tipo son filtros opcionales sobre el padrón de altas.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiltroGeneracion {

    private Integer lectivoId;
    private Integer facultadId;
    private Integer geograficaId;
    private Integer claseChequeraId;
    private Integer tipoChequeraId;
    private boolean soloNoEnviadas;
    /**
     * Si viene informado, limita el padrón a esas chequeras (generación puntual).
     */
    private List<Long> chequeraSerieIds;
    /**
     * Emisión: fuerza la emisión con deuda excedida. Requiere motivo y permiso.
     */
    private boolean forzarDeuda;
    private String motivoOverride;
    /**
     * Emisión directa (pantalla de envío): limita los borradores a esos ids.
     */
    private List<Long> borradorIds;
}
