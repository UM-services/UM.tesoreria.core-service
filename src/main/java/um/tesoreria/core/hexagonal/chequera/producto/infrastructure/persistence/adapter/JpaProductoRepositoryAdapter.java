package um.tesoreria.core.hexagonal.chequera.producto.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.producto.domain.model.Producto;
import um.tesoreria.core.hexagonal.chequera.producto.domain.ports.out.ProductoRepository;
import um.tesoreria.core.hexagonal.chequera.producto.infrastructure.persistence.mapper.ProductoMapper;
import um.tesoreria.core.hexagonal.chequera.producto.infrastructure.persistence.repository.JpaProductoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaProductoRepositoryAdapter implements ProductoRepository {

    private final JpaProductoRepository jpaProductoRepository;
    private final ProductoMapper productoMapper;

    @Override
    public List<Producto> findAll() {
        return jpaProductoRepository.findAll().stream()
                .map(productoMapper::toDomainModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Producto> findById(Integer productoId) {
        return jpaProductoRepository.findByProductoId(productoId)
                .map(productoMapper::toDomainModel);
    }
}
