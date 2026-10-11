package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.exception.CompraCotizacionTramoException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.model.CompraCotizacionTramo;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.ports.out.CompraCotizacionTramoRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.persistence.entity.CompraCotizacionTramoEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.persistence.mapper.CompraCotizacionTramoMapper;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.persistence.repository.JpaCompraCotizacionTramoRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaCompraCotizacionTramoRepositoryAdapter implements CompraCotizacionTramoRepository {

    private final JpaCompraCotizacionTramoRepository repository;
    private final CompraCotizacionTramoMapper mapper;

    @Override
    public Optional<CompraCotizacionTramo> findById(Long cotizacionTramoId) {
        return repository.findByCotizacionTramoId(cotizacionTramoId).map(mapper::toDomain);
    }

    @Override
    public List<CompraCotizacionTramo> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                // Tope ascendente, el tramo sin tope (null) al final.
                .sorted(Comparator.comparingInt(JpaCompraCotizacionTramoRepositoryAdapter::orden))
                .toList();
    }

    @Override
    @Transactional
    public CompraCotizacionTramo save(CompraCotizacionTramo tramo) {
        CompraCotizacionTramoEntity saved;
        if (tramo.getCotizacionTramoId() != null) {
            CompraCotizacionTramoEntity managed = repository.findByCotizacionTramoId(tramo.getCotizacionTramoId())
                    .orElseThrow(() -> new CompraCotizacionTramoException(tramo.getCotizacionTramoId()));
            mapper.updateEntity(tramo, managed);
            saved = repository.save(managed);
        } else {
            saved = repository.save(mapper.toEntity(tramo));
        }
        return mapper.toDomain(saved);
    }

    @Override
    public boolean existsById(Long cotizacionTramoId) {
        return repository.existsByCotizacionTramoId(cotizacionTramoId);
    }

    @Override
    @Transactional
    public void deleteById(Long cotizacionTramoId) {
        repository.deleteByCotizacionTramoId(cotizacionTramoId);
    }

    private static int orden(CompraCotizacionTramo tramo) {
        return tramo.getTopeMultiplico() == null ? Integer.MAX_VALUE : tramo.getTopeMultiplico();
    }

}
