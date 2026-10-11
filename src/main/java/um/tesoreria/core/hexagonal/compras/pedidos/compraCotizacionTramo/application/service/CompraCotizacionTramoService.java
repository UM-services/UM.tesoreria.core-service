package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.exception.CompraCotizacionTramoException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.model.CompraCotizacionTramo;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.ports.in.CreateCotizacionTramoUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.ports.in.DeleteCotizacionTramoUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.ports.in.GetAllCotizacionTramosUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.ports.in.GetCotizacionTramoByIdUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.ports.in.UpdateCotizacionTramoUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.application.service.CompraReferenciaService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.model.CompraReferencia;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompraCotizacionTramoService {

    private final GetAllCotizacionTramosUseCase getAllCotizacionTramosUseCase;
    private final GetCotizacionTramoByIdUseCase getCotizacionTramoByIdUseCase;
    private final CreateCotizacionTramoUseCase createCotizacionTramoUseCase;
    private final UpdateCotizacionTramoUseCase updateCotizacionTramoUseCase;
    private final DeleteCotizacionTramoUseCase deleteCotizacionTramoUseCase;
    // Composición cross-slice (patrón pautado): la referencia por ejercicio vive en su propio slice.
    private final CompraReferenciaService compraReferenciaService;

    public List<CompraCotizacionTramo> findAll() {
        return getAllCotizacionTramosUseCase.getAll();
    }

    public Optional<CompraCotizacionTramo> findById(Long cotizacionTramoId) {
        return getCotizacionTramoByIdUseCase.getById(cotizacionTramoId);
    }

    public CompraCotizacionTramo add(CompraCotizacionTramo tramo) {
        return createCotizacionTramoUseCase.create(tramo);
    }

    public CompraCotizacionTramo update(CompraCotizacionTramo tramo, Long cotizacionTramoId) {
        CompraCotizacionTramo updated = updateCotizacionTramoUseCase.update(tramo, cotizacionTramoId);
        if (updated == null) {
            throw new CompraCotizacionTramoException(cotizacionTramoId);
        }
        return updated;
    }

    public void delete(Long cotizacionTramoId) {
        deleteCotizacionTramoUseCase.delete(cotizacionTramoId);
    }

    /**
     * Resuelve el mínimo de cotizaciones para un monto del ejercicio: combina los tramos
     * configurados con la referencia del ejercicio. Devuelve vacío cuando la configuración no
     * permite resolver (sin referencia para el ejercicio o sin tramos que cubran el monto); el
     * llamador decide si eso bloquea (fail-closed) o no.
     */
    public Optional<Integer> resolverMinimo(Integer ejercicioId, BigDecimal monto) {
        BigDecimal referencia = compraReferenciaService.getByEjercicioId(ejercicioId)
                .map(CompraReferencia::getImporte)
                .orElse(null);
        return ResolutorMinimoCotizaciones.resolver(getAllCotizacionTramosUseCase.getAll(), referencia, monto);
    }

}

