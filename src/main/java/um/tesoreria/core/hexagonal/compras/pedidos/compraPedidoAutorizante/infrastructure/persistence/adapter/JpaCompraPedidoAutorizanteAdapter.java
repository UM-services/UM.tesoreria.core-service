package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.infrastructure.persistence.adapter;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.domain.ports.out.CompraPedidoAutorizanteRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.infrastructure.persistence.entity.CompraPedidoAutorizanteDependenciaEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.infrastructure.persistence.repository.JpaCompraPedidoAutorizanteDependenciaRepository;

import java.util.List;

/**
 * Persistencia JPA de la tabla puente autorizante ↔ dependencia.
 *
 * <p>Se migró desde SQL directo para que asignar o quitar una dependencia autorizada quede
 * auditado (alta y baja pasan por los listeners de Hibernate).</p>
 */
@Component
@RequiredArgsConstructor
public class JpaCompraPedidoAutorizanteAdapter implements CompraPedidoAutorizanteRepository {

    private final JpaCompraPedidoAutorizanteDependenciaRepository repository;

    @Override
    public List<Integer> findDependenciaIdsByAutorizanteId(Integer autorizanteId) {
        return repository.findAllByAutorizanteIdOrderByDependenciaIdAsc(autorizanteId).stream()
                .map(CompraPedidoAutorizanteDependenciaEntity::getDependenciaId)
                .toList();
    }

    @Override
    @Transactional
    public void asignar(Integer autorizanteId, Integer dependenciaId) {
        if (repository.existsByAutorizanteIdAndDependenciaId(autorizanteId, dependenciaId)) {
            return;
        }
        repository.save(new CompraPedidoAutorizanteDependenciaEntity(autorizanteId, dependenciaId));
    }

    @Override
    @Transactional
    public void quitar(Integer autorizanteId, Integer dependenciaId) {
        repository.deleteByAutorizanteIdAndDependenciaId(autorizanteId, dependenciaId);
    }

    @Override
    public boolean existe(Integer autorizanteId, Integer dependenciaId) {
        return repository.existsByAutorizanteIdAndDependenciaId(autorizanteId, dependenciaId);
    }

}
