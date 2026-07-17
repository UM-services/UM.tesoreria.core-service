package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in.GetProveedorMovimientosEliminablesUseCase;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.ProveedorMovimientoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class GetProveedorMovimientosEliminablesUseCaseImpl implements GetProveedorMovimientosEliminablesUseCase {
    private final ProveedorMovimientoRepository repository;

    @Override
    public List<ProveedorMovimiento> getProveedorMovimientosEliminables(Integer ejercicioId) {
        List<ProveedorMovimiento> anuladas = repository
                .findAllByComprobanteIdAndPrefijoAndFechaAnulacionNotNull(6, ejercicioId);
        List<ProveedorMovimiento> sueldosPendientes = repository
                .findAllByComprobanteIdAndPrefijoAndNetoSinDescuentoAndNombreBeneficiarioStartingWithAndConceptoStartingWith(
                        6, ejercicioId, BigDecimal.ZERO, "Personal", "Sueldos");
        return Stream.concat(anuladas.stream(), sueldosPendientes.stream())
                .collect(Collectors.toList());
    }
}
