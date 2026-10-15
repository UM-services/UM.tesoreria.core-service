package um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.application.exception.CompraReferenciaException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.model.CompraReferencia;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.ports.in.UpsertReferenciaUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.ports.out.CompraReferenciaRepository;

@Component
@RequiredArgsConstructor
public class UpsertReferenciaUseCaseImpl implements UpsertReferenciaUseCase {

    private final CompraReferenciaRepository repository;

    @Override
    public CompraReferencia upsert(CompraReferencia referencia) {
        if (referencia == null || referencia.getEjercicioId() == null) {
            throw new CompraReferenciaException("ejercicioId es requerido");
        }
        if (referencia.getImporte() == null || referencia.getImporte().signum() <= 0) {
            throw new CompraReferenciaException("El importe de referencia debe ser mayor a cero");
        }
        return repository.save(referencia);
    }

}
