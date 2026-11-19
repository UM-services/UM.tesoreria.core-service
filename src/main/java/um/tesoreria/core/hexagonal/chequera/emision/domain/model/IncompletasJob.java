package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncompletasJob {

    private Long jobId;
    private IncompletasEtapa etapa;
    private Long jobOrigenId;
    private Integer lectivoId;
    private Integer facultadId;
    private Integer geograficaId;
    private Integer claseChequeraId;
    private boolean soloNoEnviadas;
    private IncompletasJobEstado estado;
    private int totalTareas;
    private int tareasOk;
    private int tareasOmitidas;
    private int tareasError;
    private int tareasPendientes;
    private Long usuarioId;
    private String usuarioLogin;
    private OffsetDateTime heartbeat;
    private OffsetDateTime iniciado;
    private OffsetDateTime finalizado;
    private String mensaje;
    private Long ultimaChequeraId;
}
