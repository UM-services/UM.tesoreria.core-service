package um.tesoreria.core.hexagonal.chequera.envio.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** Filtros de un job de envío masivo: los mismos que «Revisar» de {@code frmCheqMail}. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiltroEnvio {

    private EnvioOperacion operacion;
    private Integer lectivoId;
    private Integer facultadId;
    private Integer geograficaId;
    private boolean soloNoEnviadas;
    private List<Long> chequeraIds;
    private boolean codigoBarras;
}
