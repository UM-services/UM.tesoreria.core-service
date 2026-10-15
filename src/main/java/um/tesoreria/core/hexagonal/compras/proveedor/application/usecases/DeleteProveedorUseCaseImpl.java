package um.tesoreria.core.hexagonal.compras.proveedor.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.in.DeleteProveedorUseCase;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.out.ProveedorHistorialPort;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.out.ProveedorRepository;

import java.util.Optional;

/**
 * Baja física de un proveedor. La baja y su evento de historial van en la misma transacción. Si otro
 * registro apunta al proveedor, la restricción de clave foránea de la base frena el borrado: el adaptador
 * del repositorio lo traduce a {@code ProveedorConflictoException} y la transacción se revierte junto con el
 * evento de historial.
 */
@Component
@RequiredArgsConstructor
public class DeleteProveedorUseCaseImpl implements DeleteProveedorUseCase {

    private final ProveedorRepository repository;
    private final ProveedorHistorialPort historial;
    private final TransactionOperations transaction;

    @Override
    public boolean deleteProveedor(Integer proveedorId) {
        return Boolean.TRUE.equals(transaction.execute(status -> {
            Optional<Proveedor> anterior = repository.findByProveedorId(proveedorId);
            if (anterior.isEmpty()) {
                return false;
            }
            historial.registrarBaja(anterior.get());
            return repository.deleteById(proveedorId);
        }));
    }
}