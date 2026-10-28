package um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.model.InscripcionMaterias;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.ports.out.PendienteInfoRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindInscripcionMateriasUseCaseImplTest {

    @Mock
    private PendienteInfoRepository repository;

    @InjectMocks
    private FindInscripcionMateriasUseCaseImpl useCase;

    // protects=el use case pasa los cinco datos del alumno al puerto de salida y devuelve su resultado;
    // fails_when=el use case cambia el orden de los argumentos, filtra el resultado o lo cambia por otro;
    // no_claim=la consulta HTTP a la facultad: la prueba el test del consumer.
    @Test
    void delegatesToTheOutputPortAndReturnsItsResult() {
        var inscripcion = new InscripcionMaterias(5, "Plan", "Carrera", null, List.of());
        when(repository.findByAlumno(1, 37, 2, new BigDecimal("30123456"), 3)).thenReturn(Optional.of(inscripcion));
        when(repository.findByAlumno(1, 37, 2, new BigDecimal("99"), 3)).thenReturn(Optional.empty());

        assertThat(useCase.findByAlumno(1, 37, 2, new BigDecimal("30123456"), 3)).containsSame(inscripcion);
        assertThat(useCase.findByAlumno(1, 37, 2, new BigDecimal("99"), 3)).isEmpty();
    }

    // protects=una excepción del puerto de salida llega sin cambios al caller;
    // fails_when=el use case la traga o la convierte en Optional vacío;
    // no_claim=cómo el caller la informa: es de #487.
    @Test
    void exceptionFromThePortIsNotSwallowed() {
        when(repository.findByAlumno(1, 37, 2, new BigDecimal("1"), 3)).thenThrow(new IllegalStateException("falla"));

        assertThatThrownBy(() -> useCase.findByAlumno(1, 37, 2, new BigDecimal("1"), 3))
                .isInstanceOf(IllegalStateException.class);
    }
}
