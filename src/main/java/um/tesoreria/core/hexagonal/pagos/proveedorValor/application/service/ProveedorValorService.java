package um.tesoreria.core.hexagonal.pagos.proveedorValor.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.pagos.proveedorValor.domain.model.ProveedorValor;
import um.tesoreria.core.hexagonal.pagos.proveedorValor.domain.ports.in.CreateProveedorValorUseCase;
import um.tesoreria.core.hexagonal.pagos.proveedorValor.domain.ports.in.GetProveedorValoresByProveedorMovimientoIdUseCase;

import java.util.List;

@Service("pagosProveedorValorService")
@RequiredArgsConstructor
public class ProveedorValorService {

    private final CreateProveedorValorUseCase createProveedorValorUseCase;
    private final GetProveedorValoresByProveedorMovimientoIdUseCase getProveedorValoresByProveedorMovimientoIdUseCase;

    public ProveedorValor create(ProveedorValor proveedorValor) {
        return createProveedorValorUseCase.createProveedorValor(proveedorValor);
    }

    public List<ProveedorValor> findAllByProveedorMovimientoId(Long proveedorMovimientoId) {
        return getProveedorValoresByProveedorMovimientoIdUseCase.getProveedorValores(proveedorMovimientoId);
    }
}
