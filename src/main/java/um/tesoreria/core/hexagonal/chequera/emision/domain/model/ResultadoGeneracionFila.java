package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resultado de procesar una tarea del job de generación.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoGeneracionFila {

    private GeneracionTareaEstado estado;
    private Long borradorId;
    private String codigo;
    private String mensaje;

    public static ResultadoGeneracionFila borrador(Long borradorId, boolean reutilizado) {
        return ResultadoGeneracionFila.builder()
                .estado(reutilizado ? GeneracionTareaEstado.BORRADOR_REUTILIZADO : GeneracionTareaEstado.BORRADOR_CREADO)
                .borradorId(borradorId)
                .build();
    }

    public static ResultadoGeneracionFila emitida(Long borradorId, Long chequeraSerieId) {
        return ResultadoGeneracionFila.builder()
                .estado(GeneracionTareaEstado.EMITIDA)
                .borradorId(borradorId)
                .mensaje(chequeraSerieId == null ? null : "Chequera " + chequeraSerieId + " emitida")
                .build();
    }

    public static ResultadoGeneracionFila omitida(String codigo, String mensaje) {
        return ResultadoGeneracionFila.builder()
                .estado(GeneracionTareaEstado.OMITIDA)
                .codigo(codigo)
                .mensaje(mensaje)
                .build();
    }

    public static ResultadoGeneracionFila error(String codigo, String mensaje) {
        return ResultadoGeneracionFila.builder()
                .estado(GeneracionTareaEstado.ERROR)
                .codigo(codigo)
                .mensaje(mensaje)
                .build();
    }
}
