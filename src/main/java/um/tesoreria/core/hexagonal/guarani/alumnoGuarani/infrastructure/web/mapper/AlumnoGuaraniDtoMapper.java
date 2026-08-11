package um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.personas.domicilio.infrastructure.web.mapper.DomicilioDtoMapper;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.AlumnoGuarani;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.domain.model.PersonalesResultado;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto.AlumnoGuaraniRequest;
import um.tesoreria.core.hexagonal.guarani.alumnoGuarani.infrastructure.web.dto.PersonalesResponse;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.mapper.PersonaDtoMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlumnoGuaraniDtoMapper {

    private final PersonaDtoMapper personaDtoMapper;
    private final DomicilioDtoMapper domicilioDtoMapper;

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

    public PersonalesResponse toPersonalesResponse(PersonalesResultado resultado, AlumnoGuarani alumnoGuarani) {
        log.debug("\n\nProcessing AlumnoGuaraniDtoMapper.toPersonalesResponse\n\n");
        if (resultado == null) {
            return null;
        }
        return PersonalesResponse.builder()
                .result(resultado.getResult())
                .persona(personaDtoMapper.toResponse(resultado.getPersona()))
                .domicilio(domicilioDtoMapper.toResponse(resultado.getDomicilio()))
                .alumnoGuarani(alumnoGuarani)
                .build();
    }

}
