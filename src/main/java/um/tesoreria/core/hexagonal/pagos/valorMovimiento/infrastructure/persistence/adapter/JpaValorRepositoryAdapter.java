package um.tesoreria.core.hexagonal.pagos.valorMovimiento.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.Valor;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.ports.out.ValorRepository;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.infrastructure.persistence.mapper.ValorMapper;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.infrastructure.persistence.repository.JpaValorRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaValorRepositoryAdapter implements ValorRepository {

    private final JpaValorRepository jpaRepository;
    private final ValorMapper mapper;

    @Override
    public Optional<Valor> findById(Integer valorId) {
        return jpaRepository.findById(valorId).map(mapper::toDomain);
    }
}
