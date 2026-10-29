package um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.model.Bancaria;
import um.tesoreria.core.hexagonal.contable.bancaria.domain.ports.out.BancariaHistorialPort;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.in.RegistrarEscrituraHistorialUseCase;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BancariaHistorialAdapter implements BancariaHistorialPort {
    private static final String ENTIDAD = "bancaria";

    private final RegistrarEscrituraHistorialUseCase historial;

    @Override
    public void registrarAlta(Bancaria creada) {
        historial.registrarAlta(ENTIDAD, clave(creada), estado(creada));
    }

    @Override
    public void registrarEdicion(Bancaria anterior, Bancaria nueva) {
        historial.registrarEdicion(ENTIDAD, clave(nueva), estado(anterior), estado(nueva));
    }

    @Override
    public void registrarBaja(Bancaria anterior) {
        historial.registrarBaja(ENTIDAD, clave(anterior), estado(anterior));
    }

    private static String clave(Bancaria bancaria) {
        return String.valueOf(bancaria.getBancariaId());
    }

    /** Copia del estado editable (nunca la entidad): updated, created y uuid no son datos que el usuario edite. */
    static Map<String, Object> estado(Bancaria bancaria) {
        Map<String, Object> estado = new LinkedHashMap<>();
        estado.put("cub_id", bancaria.getBancariaId());
        estado.put("banco", bancaria.getBanco());
        estado.put("numero", bancaria.getNumero());
        estado.put("cbu", bancaria.getCbu());
        estado.put("titular", bancaria.getTitular());
        estado.put("cuit", bancaria.getCuit());
        estado.put("numeroCuenta", bancaria.getNumeroCuenta());
        estado.put("proveedorId", bancaria.getProveedorId());
        return estado;
    }
}
