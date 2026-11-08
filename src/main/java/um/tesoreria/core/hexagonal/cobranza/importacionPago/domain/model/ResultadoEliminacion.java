package um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Resultado de eliminar una importación. Mantiene los mismos efectos que la operación histórica:
 * se borran los pagos del archivo, sus filas de {@code archivo_banco} y la cabecera
 * {@code archivo_importado}, y se recalculan las cuotas afectadas. No revierte débitos,
 * PayPerTIC, VISA ni {@code chequera_cuota.importe1}.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoEliminacion {

    private String filename;
    private int pagosEliminados;
    private int pagosReemplazoEliminados;
    private int archivosBancoEliminados;
    private int cuotasRecalculadas;

}
