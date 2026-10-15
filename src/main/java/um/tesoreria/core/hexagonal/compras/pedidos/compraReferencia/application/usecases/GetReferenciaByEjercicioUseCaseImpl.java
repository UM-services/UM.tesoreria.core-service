package um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.application.exception.CompraReferenciaException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.model.CompraReferencia;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.ports.in.GetReferenciaByEjercicioUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.ports.out.CompraReferenciaRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetReferenciaByEjercicioUseCaseImpl implements GetReferenciaByEjercicioUseCase {

    private final CompraReferenciaRepository repository;

    @Override
    public Optional<CompraReferencia> getByEjercicioId(Integer ejercicioId) {
        if (ejercicioId == null) {
            throw new CompraReferenciaException("ejercicioId es requerido");
        }
        return repository.findByEjercicioId(ejercicioId);
    }

}
