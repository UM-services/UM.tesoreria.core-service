package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.model.CompraCotizacionTramo;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.ports.in.CreateCotizacionTramoUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.ports.out.CompraCotizacionTramoRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CreateCotizacionTramoUseCaseImpl implements CreateCotizacionTramoUseCase {

    private final CompraCotizacionTramoRepository repository;

    @Override
    public CompraCotizacionTramo create(CompraCotizacionTramo tramo) {
        CompraCotizacionTramoValidator.validarCampos(tramo);
        List<CompraCotizacionTramo> conjunto = new ArrayList<>(repository.findAll());
        conjunto.add(tramo);
        CompraCotizacionTramoValidator.validarConjunto(conjunto);
        return repository.save(tramo);
    }

}
