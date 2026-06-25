package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.AlumnoGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto.AlumnoGuaraniRequest;

@Component
@Slf4j
public class AlumnoGuaraniDtoMapper {

    public AlumnoGuarani toDomain(AlumnoGuaraniRequest request) {
        log.debug("\n\nProcessing AlumnoGuaraniDtoMapper.toDomain\n\n");
        if (request == null) {
            return null;
        }
        return AlumnoGuarani.builder()
                .alumno(request.getAlumno())
                .legajo(request.getLegajo())
                .persona(request.getPersona())
                .propuesta(request.getPropuesta())
                .planVersion(request.getPlanVersion())
                .ubicacion(request.getUbicacion())
                .modalidad(request.getModalidad())
                .division(request.getDivision())
                .anioCursada(request.getAnioCursada())
                .cantidadReadmisiones(request.getCantidadReadmisiones())
                .regular(request.getRegular())
                .calidad(request.getCalidad())
                .coeficiente(request.getCoeficiente())
                .personaRel(request.getPersonaRel())
                .propuestaRel(request.getPropuestaRel())
                .ubicacionRel(request.getUbicacionRel())
                .build();
    }

}
