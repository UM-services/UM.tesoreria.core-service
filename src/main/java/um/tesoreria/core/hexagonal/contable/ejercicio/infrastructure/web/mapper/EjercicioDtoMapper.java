package um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
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
                .bloqueado(request.getBloqueado())
                .ordenContableResultado(request.getOrdenContableResultado())
                .ordenContableBienesUso(request.getOrdenContableBienesUso())
                .build();
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
