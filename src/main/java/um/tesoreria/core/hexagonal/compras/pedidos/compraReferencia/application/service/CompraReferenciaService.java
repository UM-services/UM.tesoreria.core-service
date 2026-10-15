package um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.model.CompraReferencia;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.ports.in.GetReferenciaByEjercicioUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.ports.in.UpsertReferenciaUseCase;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompraReferenciaService {

    private final GetReferenciaByEjercicioUseCase getReferenciaByEjercicioUseCase;
    private final UpsertReferenciaUseCase upsertReferenciaUseCase;

    public Optional<CompraReferencia> getByEjercicioId(Integer ejercicioId) {
        return getReferenciaByEjercicioUseCase.getByEjercicioId(ejercicioId);
    }

    public CompraReferencia upsert(CompraReferencia referencia) {
        return upsertReferenciaUseCase.upsert(referencia);
    }

}
