package um.tesoreria.core.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendChequeraEvent implements Serializable {
    private Integer facultadId;
    private Integer tipoChequeraId;
    private Long chequeraSerieId;
    private Integer alternativaId;
    private Boolean copiaInformes;
    private Boolean incluyeMatricula;
    private Boolean codigoBarras;
    /**
     * Campos aditivos. El envío masivo (frmCheqMail) los manda en {@code true} para usar el
     * contexto MercadoPago *bulk* y refrescar los códigos de barras antes de generar el PDF,
     * igual que hacía el legacy contra el shim {@code /sendChequera/bulk} del sender.
     */
    private Boolean bulk;
    private Boolean actualizarCodigosBarras;
}
