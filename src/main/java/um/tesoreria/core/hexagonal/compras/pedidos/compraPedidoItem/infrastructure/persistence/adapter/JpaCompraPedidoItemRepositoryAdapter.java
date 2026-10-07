package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.model.CompraPedidoItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.ports.out.CompraPedidoItemRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.infrastructure.persistence.entity.CompraPedidoItemEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.infrastructure.persistence.mapper.CompraPedidoItemMapper;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.infrastructure.persistence.repository.JpaCompraPedidoItemRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JpaCompraPedidoItemRepositoryAdapter implements CompraPedidoItemRepository {

    private final JpaCompraPedidoItemRepository jpaCompraPedidoItemRepository;
    private final CompraPedidoItemMapper compraPedidoItemMapper;

    @Override
    public List<CompraPedidoItem> saveAll(List<CompraPedidoItem> items) {
        List<CompraPedidoItemEntity> entities = items.stream()
                .map(compraPedidoItemMapper::toEntity)
                .toList();
        return jpaCompraPedidoItemRepository.saveAll(entities).stream()
                .map(compraPedidoItemMapper::toDomain)
                .toList();
    }

    @Override
    public List<CompraPedidoItem> findByCompraPedidoId(Integer compraPedidoId) {
        return jpaCompraPedidoItemRepository.findByCompraPedidoIdOrderByOrdenAsc(compraPedidoId).stream()
                .map(compraPedidoItemMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteByCompraPedidoId(Integer compraPedidoId) {
        jpaCompraPedidoItemRepository.deleteByCompraPedidoId(compraPedidoId);
    }

}
