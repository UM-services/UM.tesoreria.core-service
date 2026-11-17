package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Aviso estructurado devuelto por la emisión.
 *
 * <p>Los avisos bloqueantes impiden emitir; los informativos documentan efectos que
 * el backend no puede ejecutar (por ejemplo la sincronización con la base remota de
 * la facultad) o situaciones que el operador debe conocer.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvisoEmision {

    private String codigo;
    private String mensaje;
    private boolean bloqueante;

    public static AvisoEmision bloqueante(String codigo, String mensaje) {
        return AvisoEmision.builder().codigo(codigo).mensaje(mensaje).bloqueante(true).build();
    }

    public static AvisoEmision informativo(String codigo, String mensaje) {
        return AvisoEmision.builder().codigo(codigo).mensaje(mensaje).bloqueante(false).build();
    }
}
