package um.tesoreria.core.hexagonal.pagos.proveedorValor.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.proveedorValor.domain.model.ProveedorValor;
import um.tesoreria.core.hexagonal.pagos.proveedorValor.domain.ports.in.CreateProveedorValorUseCase;
import um.tesoreria.core.hexagonal.pagos.proveedorValor.domain.ports.out.ProveedorValorRepository;

@Component
@RequiredArgsConstructor
public class CreateProveedorValorUseCaseImpl implements CreateProveedorValorUseCase {

    private final ProveedorValorRepository proveedorValorRepository;

    @Override
    public ProveedorValor createProveedorValor(ProveedorValor proveedorValor) {
        return proveedorValorRepository.save(proveedorValor);
    }
}
