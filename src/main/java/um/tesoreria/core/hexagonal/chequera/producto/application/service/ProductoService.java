package um.tesoreria.core.hexagonal.chequera.producto.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.chequera.producto.application.exception.ProductoException;
import um.tesoreria.core.hexagonal.chequera.producto.domain.model.Producto;
import um.tesoreria.core.hexagonal.chequera.producto.domain.ports.in.GetAllProductosUseCase;
import um.tesoreria.core.hexagonal.chequera.producto.domain.ports.in.GetProductoByIdUseCase;
import um.tesoreria.core.model.view.ProductoTipoChequera;
import um.tesoreria.core.repository.view.ProductoTipoChequeraRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final GetAllProductosUseCase getAllProductosUseCase;
    private final GetProductoByIdUseCase getProductoByIdUseCase;
    private final ProductoTipoChequeraRepository productoTipoChequeraRepository;

    public List<Producto> findAll() {
        return getAllProductosUseCase.getAllProductos();
    }

    public List<ProductoTipoChequera> findAllByTipoChequera(Integer facultadId, Integer lectivoId,
                                                            Integer tipochequeraId) {
        return productoTipoChequeraRepository.findAllByFacultadIdAndLectivoIdAndTipochequeraId(
                facultadId, lectivoId, tipochequeraId);
    }

    public Producto findByProductoId(Integer productoId) {
        return getProductoByIdUseCase.getProductoById(productoId)
                .orElseThrow(() -> new ProductoException(productoId));
    }

}
