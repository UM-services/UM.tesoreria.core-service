package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiltroIncompletas {

    private Integer lectivoId;
    private Integer facultadId;
    private Integer geograficaId;
    private Integer claseChequeraId;
    private boolean soloNoEnviadas;
    /**
     * Si viene informado, limita el padrón a esas chequeras (reparación puntual).
     */
    private List<Long> chequeraIds;
}
