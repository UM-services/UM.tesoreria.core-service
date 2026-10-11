package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraAdjudicacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.CompraAdjudicacionRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.mapper.CompraAdjudicacionMapper;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.repository.JpaCompraAdjudicacionRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaCompraAdjudicacionRepositoryAdapter implements CompraAdjudicacionRepository {

    private final JpaCompraAdjudicacionRepository repository;
    private final CompraAdjudicacionMapper mapper;

    @Override
    public CompraAdjudicacion save(CompraAdjudicacion adjudicacion) {
        return mapper.toDomain(repository.save(mapper.toEntity(adjudicacion)));
    }

    @Override
    public Optional<CompraAdjudicacion> findByCompraCotizacionId(Long compraCotizacionId) {
        return repository.findFirstByCompraCotizacionIdOrderByCompraAdjudicacionIdDesc(compraCotizacionId)
                .map(mapper::toDomain);
    }
}
