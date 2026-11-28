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
public class GeneracionJob {

    private Long jobId;
    private GeneracionEtapa etapa;
    private Long jobOrigenId;
    private Integer lectivoId;
    private Integer facultadId;
    private Integer geograficaId;
    private Integer claseChequeraId;
    private Integer tipoChequeraId;
    private boolean soloNoEnviadas;
    private boolean forzarDeuda;
    private String motivoOverride;
    private GeneracionJobEstado estado;
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
