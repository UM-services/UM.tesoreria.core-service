package um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out.EjercicioHistorialPort;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.in.RegistrarEscrituraHistorialUseCase;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EjercicioHistorialAdapter implements EjercicioHistorialPort {
    private static final String ENTIDAD = "ejercicio";

    private final RegistrarEscrituraHistorialUseCase historial;

    @Override
    public void registrarAlta(Ejercicio creado) {
        historial.registrarAlta(ENTIDAD, clave(creado), estado(creado));
    }

    @Override
    public void registrarEdicion(Ejercicio anterior, Ejercicio nuevo) {
        historial.registrarEdicion(ENTIDAD, clave(nuevo), estado(anterior), estado(nuevo));
    }

    @Override
    public void registrarBaja(Ejercicio anterior) {
        historial.registrarBaja(ENTIDAD, clave(anterior), estado(anterior));
    }

    private static String clave(Ejercicio ejercicio) {
        return String.valueOf(ejercicio.getEjercicioId());
    }

    static Map<String, Object> estado(Ejercicio ejercicio) {
        Map<String, Object> estado = new LinkedHashMap<>();
        estado.put("eje_id", ejercicio.getEjercicioId());
        estado.put("nombre", ejercicio.getNombre());
        estado.put("fechaInicio", ejercicio.getFechaInicio());
        estado.put("fechaFinal", ejercicio.getFechaFinal());
        estado.put("bloqueado", ejercicio.getBloqueado());
        estado.put("ordenContableResultado", ejercicio.getOrdenContableResultado());
        estado.put("ordenContableBienesUso", ejercicio.getOrdenContableBienesUso());
        return estado;
    }
}
