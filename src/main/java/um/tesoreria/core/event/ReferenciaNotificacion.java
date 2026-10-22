package um.tesoreria.core.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Referencia al objeto de negocio que originó la notificación. Viaja en el evento para que el
 * sender pueda informar el resultado y core actualice el estado correspondiente.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferenciaNotificacion {

    private String tipo;
    private String id;

}
