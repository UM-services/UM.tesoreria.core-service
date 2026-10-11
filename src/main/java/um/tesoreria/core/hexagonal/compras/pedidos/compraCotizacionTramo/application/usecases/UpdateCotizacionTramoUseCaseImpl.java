package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.model.CompraCotizacionTramo;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.ports.in.UpdateCotizacionTramoUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.ports.out.CompraCotizacionTramoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UpdateCotizacionTramoUseCaseImpl implements UpdateCotizacionTramoUseCase {

    private final CompraCotizacionTramoRepository repository;

    @Override
    public CompraCotizacionTramo update(CompraCotizacionTramo datos, Long cotizacionTramoId) {
        CompraCotizacionTramoValidator.validarCampos(datos);
        return repository.findById(cotizacionTramoId).map(actual -> {
            if (datos.getNombre() != null) {
                actual.setNombre(datos.getNombre());
            }
            // El tope se setea siempre: permite moverlo o limpiarlo a nulo (tramo sin tope).
            actual.setTopeMultiplico(datos.getTopeMultiplico());
            if (datos.getCantidadMinima() != null) {
                actual.setCantidadMinima(datos.getCantidadMinima());
            }
            if (datos.getActivo() != null) {
                actual.setActivo(datos.getActivo());
            }
            List<CompraCotizacionTramo> conjunto = repository.findAll().stream()
                    .filter(t -> !cotizacionTramoId.equals(t.getCotizacionTramoId()))
                    .collect(Collectors.toCollection(ArrayList::new));
            conjunto.add(actual);
            CompraCotizacionTramoValidator.validarConjunto(conjunto);
            return repository.save(actual);
        }).orElse(null);
    }

}
