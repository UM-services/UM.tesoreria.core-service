package um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioSolicitudInvalidaException;

import java.math.BigDecimal;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.EjercicioFechas;
import um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.web.dto.EjercicioRequest;
import um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.web.dto.EjercicioResponse;

@Component
public class EjercicioDtoMapper {

    public Ejercicio toDomain(EjercicioRequest request) {
        if (request == null) return null;
        return Ejercicio.builder()
                .ejercicioId(request.getEjercicioId())
                .nombre(request.getNombre())
                .fechaInicio(EjercicioFechas.aDia(request.getFechaInicio()))
                .fechaFinal(EjercicioFechas.aDia(request.getFechaFinal()))
                .bloqueado(aByte("bloqueado", request.getBloqueado()))
                .ordenContableResultado(entero("ordenContableResultado", request.getOrdenContableResultado(), Integer.MIN_VALUE, Integer.MAX_VALUE))
                .ordenContableBienesUso(entero("ordenContableBienesUso", request.getOrdenContableBienesUso(), Integer.MIN_VALUE, Integer.MAX_VALUE))
                .build();
    }

    /** Un entero exacto dentro del rango del tipo destino; un decimal o un desborde es una solicitud inválida, nunca se trunca. */
    private static Integer entero(String campo, BigDecimal valor, int minimo, int maximo) {
        if (valor == null) {
            return null;
        }
        try {
            int exacto = valor.intValueExact();
            if (exacto < minimo || exacto > maximo) {
                throw new ArithmeticException();
            }
            return exacto;
        } catch (ArithmeticException ex) {
            throw new EjercicioSolicitudInvalidaException(campo + " tiene que ser un entero entre " + minimo + " y " + maximo);
        }
    }

    private static Byte aByte(String campo, BigDecimal valor) {
        Integer entero = entero(campo, valor, Byte.MIN_VALUE, Byte.MAX_VALUE);
        return entero == null ? null : entero.byteValue();
    }

    public EjercicioResponse toResponse(Ejercicio domain) {
        if (domain == null) return null;
        EjercicioResponse response = new EjercicioResponse();
        response.setEjercicioId(domain.getEjercicioId());
        response.setNombre(domain.getNombre());
        response.setFechaInicio(EjercicioFechas.aInicioDeDia(domain.getFechaInicio()));
        response.setFechaFinal(EjercicioFechas.aInicioDeDia(domain.getFechaFinal()));
        response.setBloqueado(domain.getBloqueado());
        response.setOrdenContableResultado(domain.getOrdenContableResultado());
        response.setOrdenContableBienesUso(domain.getOrdenContableBienesUso());
        return response;
    }
}
