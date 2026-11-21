package um.tesoreria.core.hexagonal.chequera.envio.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Resultado de una tarea, tal como se guarda en {@code chequera_envio_job_tarea}. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoEnvio {

    private EnvioTareaEstado estado;
    private String codigo;
    private String mensaje;
    private String detalle;
    private Boolean retenida;
    private Boolean enviado;

    public static ResultadoEnvio de(EnvioTareaEstado estado, String codigo, String mensaje) {
        return ResultadoEnvio.builder().estado(estado).codigo(codigo).mensaje(mensaje).build();
    }

    public static ResultadoEnvio error(String codigo, String mensaje) {
        return de(EnvioTareaEstado.ERROR, codigo, mensaje);
    }
}
