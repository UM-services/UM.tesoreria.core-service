package um.tesoreria.core.hexagonal.chequera.envio.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnvioJobTarea {

    private Long tareaId;
    private Long jobId;
    private Long chequeraId;
    private BigDecimal personaId;
    private Integer documentoId;
    private Integer facultadId;
    private Integer tipoChequeraId;
    private Long chequeraSerieId;
    private String personaNombre;
    private EnvioTareaEstado estado;
    private String codigo;
    private String mensaje;
    private String detalle;
    private Boolean retenida;
    private Boolean enviado;
    private OffsetDateTime finalizado;
}
