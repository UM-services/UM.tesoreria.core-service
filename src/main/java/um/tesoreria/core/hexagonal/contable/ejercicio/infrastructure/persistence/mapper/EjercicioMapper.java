package um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.EjercicioFechas;
import um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.entity.EjercicioEntity;

@Component
public class EjercicioMapper {

    public EjercicioEntity toEntity(Ejercicio domain) {
        if (domain == null) return null;
        EjercicioEntity.EjercicioEntityBuilder builder = EjercicioEntity.builder()
                .ejercicioId(domain.getEjercicioId())
                .nombre(domain.getNombre())
                .fechaInicio(EjercicioFechas.aInicioDeDia(domain.getFechaInicio()))
                .fechaFinal(EjercicioFechas.aInicioDeDia(domain.getFechaFinal()));
        if (domain.getBloqueado() != null) builder.bloqueado(domain.getBloqueado());
        if (domain.getOrdenContableResultado() != null) builder.ordenContableResultado(domain.getOrdenContableResultado());
        if (domain.getOrdenContableBienesUso() != null) builder.ordenContableBienesUso(domain.getOrdenContableBienesUso());
        return builder.build();
    }

    public Ejercicio toDomain(EjercicioEntity entity) {
        if (entity == null) return null;
        return Ejercicio.builder()
                .ejercicioId(entity.getEjercicioId())
                .nombre(entity.getNombre())
                .fechaInicio(EjercicioFechas.aDia(entity.getFechaInicio()))
                .fechaFinal(EjercicioFechas.aDia(entity.getFechaFinal()))
                .bloqueado(entity.getBloqueado())
                .ordenContableResultado(entity.getOrdenContableResultado())
                .ordenContableBienesUso(entity.getOrdenContableBienesUso())
                .build();
    }
}
