package um.tesoreria.core.hexagonal.pagos.proveedorValor.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.proveedorValor.domain.model.ProveedorValor;
import um.tesoreria.core.hexagonal.pagos.proveedorValor.domain.ports.in.GetProveedorValoresByProveedorMovimientoIdUseCase;
import um.tesoreria.core.hexagonal.pagos.proveedorValor.domain.ports.out.ProveedorValorRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetProveedorValoresByProveedorMovimientoIdUseCaseImpl
        implements GetProveedorValoresByProveedorMovimientoIdUseCase {

    private final ProveedorValorRepository proveedorValorRepository;

    @Override
    public List<ProveedorValor> getProveedorValores(Long proveedorMovimientoId) {
        return proveedorValorRepository.findAllByProveedorMovimientoId(proveedorMovimientoId);
    }
}
