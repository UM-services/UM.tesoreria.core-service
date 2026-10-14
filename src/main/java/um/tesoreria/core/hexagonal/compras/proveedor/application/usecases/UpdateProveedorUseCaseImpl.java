package um.tesoreria.core.hexagonal.compras.proveedor.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.compras.proveedor.application.exception.ProveedorConflictoException;
import um.tesoreria.core.hexagonal.compras.proveedor.application.exception.ProveedorInvalidoException;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.ProveedorCuit;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.in.UpdateProveedorUseCase;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.out.ProveedorHistorialPort;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.out.ProveedorRepository;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Edición de proveedor. Un campo ausente conserva el valor guardado y {@code ""} lo borra. La cuenta
 * contable es obligatoria sobre el resultado. El CUIT repetido solo se controla si el CUIT cambia, así un
 * proveedor con CUIT histórico duplicado se puede seguir editando. Todo va en una transacción, junto con el
 * evento de historial.
 *
 * <p>Sin índice único en la base el control es "consultar y luego guardar": dos ediciones simultáneas al
 * mismo CUIT podrían duplicarse. Es una limitación conocida y aceptada.</p>
 */
@Component
@RequiredArgsConstructor
public class UpdateProveedorUseCaseImpl implements UpdateProveedorUseCase {

    private final ProveedorRepository repository;
    private final ProveedorHistorialPort historial;
    private final TransactionOperations transaction;

    @Override
    public Optional<Proveedor> updateProveedor(Integer proveedorId, Proveedor pedido) {
        return transaction.execute(status -> {
            // Se lee y se fusiona dentro de la transacción: es el estado "anterior" del historial y evita
            // pisar con datos viejos un cambio hecho entre la lectura y el guardado.
            Optional<Proveedor> anterior = repository.findByProveedorId(proveedorId);
            if (anterior.isEmpty()) {
                return Optional.empty();
            }
            Proveedor fusionado = fusionar(anterior.get(), pedido);
            exigirCuenta(fusionado.getNumeroCuenta());

            if (cuitControlable(anterior.get(), fusionado)
                    && repository.existsOtroConCuit(ProveedorCuit.clave(fusionado.getCuit()), proveedorId)) {
                throw new ProveedorConflictoException(
                        "Ya existe otro proveedor con el CUIT " + ProveedorCuit.clave(fusionado.getCuit()) + ".");
            }

            Optional<Proveedor> actualizado = repository.update(proveedorId, fusionado);
            actualizado.ifPresent(nuevo -> historial.registrarEdicion(anterior.get(), nuevo));
            return actualizado;
        });
    }

    /** El CUIT cambió respecto del guardado y trae dato: solo en ese caso se controla el duplicado. */
    private static boolean cuitControlable(Proveedor guardado, Proveedor nuevo) {
        String cuitNuevo = ProveedorCuit.clave(nuevo.getCuit());
        return !cuitNuevo.equals(ProveedorCuit.clave(guardado.getCuit())) && !ProveedorCuit.sinDato(cuitNuevo);
    }

    /** VB6 ({@code frmProveedores}) solo bloquea la cuenta nula; {@code 0} no cuenta como faltante. */
    private static void exigirCuenta(BigDecimal numeroCuenta) {
        if (numeroCuenta == null) {
            throw new ProveedorInvalidoException("Falta la cuenta contable del proveedor.");
        }
    }

    /**
     * Combina el pedido con lo guardado: un campo ausente ({@code null}) conserva el valor existente y un
     * {@code ""} lo borra. VB6 manda siempre los campos completos y usa {@code ""} para vaciar.
     */
    private static Proveedor fusionar(Proveedor guardado, Proveedor pedido) {
        return Proveedor.builder()
                .proveedorId(guardado.getProveedorId())
                .cuit(elegir(pedido.getCuit(), guardado.getCuit()))
                .nombreFantasia(elegir(pedido.getNombreFantasia(), guardado.getNombreFantasia()))
                .razonSocial(elegir(pedido.getRazonSocial(), guardado.getRazonSocial()))
                .ordenCheque(elegir(pedido.getOrdenCheque(), guardado.getOrdenCheque()))
                .domicilio(elegir(pedido.getDomicilio(), guardado.getDomicilio()))
                .telefono(elegir(pedido.getTelefono(), guardado.getTelefono()))
                .fax(elegir(pedido.getFax(), guardado.getFax()))
                .celular(elegir(pedido.getCelular(), guardado.getCelular()))
                .email(elegir(pedido.getEmail(), guardado.getEmail()))
                .emailInterno(elegir(pedido.getEmailInterno(), guardado.getEmailInterno()))
                .numeroCuenta(elegir(pedido.getNumeroCuenta(), guardado.getNumeroCuenta()))
                .habilitado(elegir(pedido.getHabilitado(), guardado.getHabilitado()))
                .cbu(elegir(pedido.getCbu(), guardado.getCbu()))
                .build();
    }

    private static <T> T elegir(T pedido, T guardado) {
        return pedido != null ? pedido : guardado;
    }
}