package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.domain.model.CompraPedidoHistorial;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.domain.ports.out.CompraPedidoHistorialRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.infrastructure.persistence.mapper.CompraPedidoHistorialMapper;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.infrastructure.persistence.repository.JpaCompraPedidoHistorialRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JpaCompraPedidoHistorialRepositoryAdapter implements CompraPedidoHistorialRepository {

    private final JpaCompraPedidoHistorialRepository jpaCompraPedidoHistorialRepository;
    private final CompraPedidoHistorialMapper compraPedidoHistorialMapper;

    @Override
    public CompraPedidoHistorial save(CompraPedidoHistorial historial) {
        return compraPedidoHistorialMapper.toDomain(
                jpaCompraPedidoHistorialRepository.save(compraPedidoHistorialMapper.toEntity(historial)));
    }

    @Override
    public List<CompraPedidoHistorial> findByPedido(Integer compraPedidoId) {
        return jpaCompraPedidoHistorialRepository.findByCompraPedidoIdOrderByFechaAsc(compraPedidoId).stream()
                .map(compraPedidoHistorialMapper::toDomain)
                .toList();
    }

}
