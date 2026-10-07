package um.tesoreria.core.hexagonal.ubicacionArticulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.in.RegistrarEscrituraHistorialUseCase;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.in.DeleteUbicacionArticulosByArticuloUseCase;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.out.UbicacionArticuloRepository;

import java.util.List;

/**
 * Solo dentro de la transacción de la baja del artículo, que ya tiene el artículo bloqueado: el orden de bloqueo
 * es siempre artículo y después vínculos. Sin esa transacción, borrar vínculos sueltos no tiene sentido.
 */
@Component
@RequiredArgsConstructor
public class DeleteUbicacionArticulosByArticuloUseCaseImpl implements DeleteUbicacionArticulosByArticuloUseCase {
    private final UbicacionArticuloRepository repository;
    // Excepción cross-slice autorizada: historial #404 en la misma transacción (RegistrarEscrituraHistorialUseCase es MANDATORY)
    private final RegistrarEscrituraHistorialUseCase registrarEscrituraHistorialUseCase;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public List<UbicacionArticulo> deleteByArticuloId(Long articuloId) {
        var borrados = repository.deleteAllByArticuloId(articuloId);
        for (var vinculo : borrados) {
            var antes = UbicacionArticuloEstado.de(vinculo);
            registrarEscrituraHistorialUseCase.registrarBaja(UbicacionArticuloEstado.ENTIDAD, antes.clave(), antes);
        }
        return borrados;
    }
}
