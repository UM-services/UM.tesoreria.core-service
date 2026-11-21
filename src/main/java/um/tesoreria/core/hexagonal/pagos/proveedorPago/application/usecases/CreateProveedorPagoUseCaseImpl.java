package um.tesoreria.core.hexagonal.pagos.proveedorPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.model.ProveedorPago;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.ports.in.CreateProveedorPagoUseCase;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.ports.out.ProveedorPagoRepository;

@Component
@RequiredArgsConstructor
public class CreateProveedorPagoUseCaseImpl implements CreateProveedorPagoUseCase {

    private final ProveedorPagoRepository proveedorPagoRepository;

    @Override
    public ProveedorPago createProveedorPago(ProveedorPago proveedorPago) {
        return proveedorPagoRepository.save(proveedorPago);
    }
}
