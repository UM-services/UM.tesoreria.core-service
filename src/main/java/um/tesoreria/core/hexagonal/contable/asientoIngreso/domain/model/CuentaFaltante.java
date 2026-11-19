package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Cuenta faltante del asiento de ingreso: el pago no tiene cuenta de imputación
 * ({@code lectivo_total_imp}) para su producto. El legacy la usa para pedir que se complete la
 * imputación; la pantalla sólo la informa.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaFaltante {

    private Integer facultadId;
    private String facultadNombre;
    private Integer lectivoId;
    private String lectivoNombre;
    private Integer tipoChequeraId;
    private String tipoChequeraNombre;
    private Integer productoId;
    private String productoNombre;
    private Integer geograficaId;
    private String geograficaNombre;

    /** Clave interna del legacy ({@code k.fac.lec.tch.pro}); sólo se usa para deduplicar. */
    public String clave() {
        return facultadId + "." + lectivoId + "." + tipoChequeraId + "." + productoId;
    }

}
