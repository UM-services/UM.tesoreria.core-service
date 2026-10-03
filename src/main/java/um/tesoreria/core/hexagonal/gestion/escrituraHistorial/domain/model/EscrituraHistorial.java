package um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Evento de historial de una escritura (alta, edición o baja).
 * Los valores anteriores/nuevos son sensibles: no deben loguearse ni exponerse
 * en respuestas públicas. {@code created}/{@code updated} de {@code Auditable}
 * en las entidades de negocio no sustituyen este historial.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EscrituraHistorial {

    private Long escrituraHistorialId;
    private LocalDateTime fecha;
    private EscrituraOperacion operacion;
    private String entidad;
    private String entidadClave;
    private String valorAnterior;
    private String valorNuevo;

    /**
     * Resumen seguro para diagnóstico (sin valores de negocio).
     */
    public String resumenSeguro() {
        return "EscrituraHistorial[id=%s, operacion=%s, entidad=%s]"
                .formatted(escrituraHistorialId, operacion, entidad);
    }
}
