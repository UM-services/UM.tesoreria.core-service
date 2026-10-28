package um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.model.InscripcionMaterias;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.ports.in.FindInscripcionMateriasUseCase;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PendienteInfoFacultadServiceTest {

    @Mock
    private FindInscripcionMateriasUseCase useCase;

    @InjectMocks
    private PendienteInfoFacultadService service;

    // protects=el service entrega al use case los cinco datos del alumno sin cambios;
    // fails_when=el service cambia el orden de los argumentos o descarta el resultado;
    // no_claim=reglas de negocio de materias: el service no las tiene (son de #487).
    @Test
    void delegatesToTheUseCase() {
        var inscripcion = new InscripcionMaterias(5, "Plan", "Carrera", null, List.of());
        when(useCase.findByAlumno(1, 37, 2, new BigDecimal("30123456"), 3)).thenReturn(Optional.of(inscripcion));

        assertThat(service.findByAlumno(1, 37, 2, new BigDecimal("30123456"), 3)).containsSame(inscripcion);
    }
}
