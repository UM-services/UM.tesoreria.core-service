package um.tesoreria.core.hexagonal.compras.proveedor.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.compras.proveedor.application.exception.ProveedorConflictoException;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.ProveedorCuit;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.in.CreateProveedorUseCase;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.out.ProveedorHistorialPort;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.out.ProveedorRepository;

/**
 * Alta de proveedor. Regla (equivalente a {@code frmProveedores}): CUIT no repetido. El CUIT sin dato ({@code 00-00000000-0}) se permite repetido. El control, el insert y el
 * evento de historial van en la misma transacción.
 *
 * <p>Sin índice único en la base el control es "consultar y luego insertar": dos altas simultáneas con el
 * mismo CUIT podrían duplicarse. Es una limitación conocida y aceptada.</p>
 */
@Component
@RequiredArgsConstructor
public class CreateProveedorUseCaseImpl implements CreateProveedorUseCase {

    private final ProveedorRepository repository;
    private final ProveedorHistorialPort historial;
    private final TransactionOperations transaction;

    @Override
    public Proveedor createProveedor(Proveedor proveedor) {
        if (proveedor.getEmailInterno() == null) {
            proveedor.setEmailInterno("");
        }
        if (proveedor.getHabilitado() == null) {
            proveedor.setHabilitado((byte) 0);
        }

        String cuit = ProveedorCuit.clave(proveedor.getCuit());
        boolean controlarCuit = !ProveedorCuit.sinDato(cuit);
        return transaction.execute(status -> {
            if (controlarCuit && repository.existsOtroConCuit(cuit, null)) {
                throw new ProveedorConflictoException("Ya existe un proveedor con el CUIT " + cuit + ".");
            }
            Proveedor creado = repository.create(proveedor);
            historial.registrarAlta(creado);
            return creado;
        });
    }
}