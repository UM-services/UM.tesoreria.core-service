package um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.out.ProveedorHistorialPort;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.in.RegistrarEscrituraHistorialUseCase;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registra las escrituras de proveedor en el historial transaccional (#404). Pasa una copia plana de los campos
 * propios del proveedor (no la entidad ni la cuenta anidada), como pide el contrato del historial.
 */
@Component
@RequiredArgsConstructor
public class ProveedorHistorialAdapter implements ProveedorHistorialPort {

    static final String ENTIDAD = "proveedor";

    private final RegistrarEscrituraHistorialUseCase historial;

    @Override
    public void registrarAlta(Proveedor creado) {
        historial.registrarAlta(ENTIDAD, clave(creado), estado(creado));
    }

    @Override
    public void registrarEdicion(Proveedor anterior, Proveedor nuevo) {
        historial.registrarEdicion(ENTIDAD, clave(nuevo), estado(anterior), estado(nuevo));
    }

    @Override
    public void registrarBaja(Proveedor anterior) {
        historial.registrarBaja(ENTIDAD, clave(anterior), estado(anterior));
    }

    private static String clave(Proveedor proveedor) {
        return String.valueOf(proveedor.getProveedorId());
    }

    static Map<String, Object> estado(Proveedor p) {
        Map<String, Object> estado = new LinkedHashMap<>();
        estado.put("proveedorId", p.getProveedorId());
        estado.put("cuit", p.getCuit());
        estado.put("nombreFantasia", p.getNombreFantasia());
        estado.put("razonSocial", p.getRazonSocial());
        estado.put("ordenCheque", p.getOrdenCheque());
        estado.put("domicilio", p.getDomicilio());
        estado.put("telefono", p.getTelefono());
        estado.put("fax", p.getFax());
        estado.put("celular", p.getCelular());
        estado.put("email", p.getEmail());
        estado.put("emailInterno", p.getEmailInterno());
        estado.put("numeroCuenta", p.getNumeroCuenta());
        estado.put("habilitado", p.getHabilitado());
        estado.put("cbu", p.getCbu());
        return estado;
    }
}