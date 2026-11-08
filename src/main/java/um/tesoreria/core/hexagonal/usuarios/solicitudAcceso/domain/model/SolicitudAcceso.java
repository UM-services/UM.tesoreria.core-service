package um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Pedido de habilitación de un permiso hecho desde el frontend.
 *
 * <p>El pedido es sólo trazabilidad: no otorga la clave por sí mismo. Un
 * administrador lo resuelve y, si corresponde, asigna el permiso desde la
 * pantalla de Usuarios/Permisos.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudAcceso {

    private Long solicitudAccesoId;
    private Long userId;
    @Builder.Default
    private String aplicacion = "TESORERIA";
    /** Clave `modulo.accion` pedida; null si el pedido es por módulo. */
    private String clave;
    private String modulo;
    private String comentario;
    @Builder.Default
    private String estado = EstadoSolicitudAcceso.PENDIENTE.name();
    private String observacion;
    private Long resueltoPor;
    private LocalDateTime resuelto;
    private LocalDateTime created;
    private LocalDateTime updated;
}
