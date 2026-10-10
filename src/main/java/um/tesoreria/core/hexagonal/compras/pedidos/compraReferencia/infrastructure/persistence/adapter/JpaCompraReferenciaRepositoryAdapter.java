package um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.application.exception.CompraReferenciaException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.model.CompraReferencia;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.ports.out.CompraReferenciaRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.persistence.entity.CompraReferenciaEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.persistence.mapper.CompraReferenciaMapper;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.persistence.repository.JpaCompraReferenciaRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaCompraReferenciaRepositoryAdapter implements CompraReferenciaRepository {

    private final JpaCompraReferenciaRepository repository;
    private final CompraReferenciaMapper mapper;

    @Override
    public Optional<CompraReferencia> findByEjercicioId(Integer ejercicioId) {
        return repository.findByEjercicioId(ejercicioId).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public CompraReferencia save(CompraReferencia referencia) {
        CompraReferenciaEntity saved;
        if (referencia.getEjercicioId() != null && repository.existsById(referencia.getEjercicioId())) {
            CompraReferenciaEntity managed = repository.findById(referencia.getEjercicioId())
                    .orElseThrow(() -> new CompraReferenciaException(referencia.getEjercicioId()));
            mapper.updateEntity(referencia, managed);
            saved = repository.save(managed);
        } else {
            saved = repository.save(mapper.toEntity(referencia));
        }
        return mapper.toDomain(saved);
    }

}
