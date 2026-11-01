package um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.adapter;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.ports.out.CuentaHistorialPort;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.in.RegistrarEscrituraHistorialUseCase;

@Component
@RequiredArgsConstructor
public class CuentaHistorialAdapter implements CuentaHistorialPort {
    private final RegistrarEscrituraHistorialUseCase historial;

    @Override
    public void registrarAlta(Cuenta creada) {
        historial.registrarAlta("cuenta", clave(creada), estado(creada));
    }

    @Override
    public void registrarEdicion(Cuenta anterior, Cuenta nueva) {
        historial.registrarEdicion("cuenta", clave(nueva), estado(anterior), estado(nueva));
    }

    @Override
    public void registrarBaja(Cuenta anterior) {
        historial.registrarBaja("cuenta", clave(anterior), estado(anterior));
    }

    private static String clave(Cuenta cuenta) {
        return cuenta.getNumeroCuenta().stripTrailingZeros().toPlainString();
    }

    static Map<String, Object> estado(Cuenta cuenta) {
        Map<String, Object> estado = new LinkedHashMap<>();
        estado.put("numeroCuenta", cuenta.getNumeroCuenta());
        estado.put("nombre", cuenta.getNombre());
        estado.put("integradora", cuenta.getIntegradora());
        estado.put("grado", cuenta.getGrado());
        estado.put("grado1", cuenta.getGrado1());
        estado.put("grado2", cuenta.getGrado2());
        estado.put("grado3", cuenta.getGrado3());
        estado.put("grado4", cuenta.getGrado4());
        estado.put("geograficaId", cuenta.getGeograficaId());
        estado.put("fechaBloqueo", cuenta.getFechaBloqueo());
        estado.put("visible", cuenta.getVisible());
        estado.put("cuentaContableId", cuenta.getCuentaContableId());
        return estado;
    }
}
