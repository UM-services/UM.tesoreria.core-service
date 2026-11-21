package um.tesoreria.core.hexagonal.chequera.envio.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvioJob {

    private Long jobId;
    private EnvioOperacion operacion;
    private Integer lectivoId;
    private Integer facultadId;
    private Integer geograficaId;
    private boolean soloNoEnviadas;
    private boolean codigoBarras;
    private int umbralCuotasDeuda;
    private EnvioJobEstado estado;
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
