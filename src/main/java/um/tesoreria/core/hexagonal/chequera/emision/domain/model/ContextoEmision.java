package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Contexto necesario para iniciar la emisión individual de una chequera:
 * persona, domicilio, deuda, series existentes del lectivo y permisos efectivos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContextoEmision {

    private Integer lectivoId;
    private PersonaEmision persona;
    private DomicilioEmision domicilio;
    private DeudaEmision deuda;
    private PermisosEmision permisos;
    @Builder.Default
    private List<SerieEmision> series = new ArrayList<>();
    @Builder.Default
    private List<AvisoEmision> avisos = new ArrayList<>();
}
