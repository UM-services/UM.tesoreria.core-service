package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Filtros de un job masivo. Sin query params: la operación y los filtros viajan en el body para que
 * «sólo no enviadas» y la selección opcional tengan un contrato explícito.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvioJobRequest {

    /** CAPTURAR_CORREOS | EXTENDER_DEBITOS | MAILING */
    private String operacion;
    private Integer lectivoId;
    private Integer facultadId;
    private Integer geograficaId;
    @Builder.Default
    private boolean soloNoEnviadas = false;
    private List<Long> chequeraIds;
    @Builder.Default
    private boolean codigoBarras = false;
}
