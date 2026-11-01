package um.tesoreria.core.event;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Solicitud de envío de una cuota. Lleva sólo el ID: el sender resuelve el resto de los datos y
 * compone el correo con su plantilla.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendCuotaEvent implements Serializable {

    private Long chequeraCuotaId;

}
