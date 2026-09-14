package um.tesoreria.core.hexagonal.personas.persona.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;
import um.tesoreria.core.hexagonal.personas.persona.domain.ports.out.PersonaRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavePersonaUseCaseImplTest {

    @Mock
    private PersonaRepository repository;

    @InjectMocks
    private SavePersonaUseCaseImpl useCase;

    @Test
    void createNormalizaApellidoYNombreAntesDeGuardar() {
        Persona persona = Persona.builder()
                .personaId(new BigDecimal("1234567"))
                .documentoId(1)
                .apellido("garcía de la cruz")
                .nombre("JUAN pablo")
                .build();
        when(repository.save(any(Persona.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.create(persona);

        ArgumentCaptor<Persona> captor = ArgumentCaptor.forClass(Persona.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getApellido()).isEqualTo("GARCÍA DE LA CRUZ");
        assertThat(captor.getValue().getNombre()).isEqualTo("Juan Pablo");
    }

    @Test
    void updateNormalizaApellidoYNombreAntesDeGuardar() {
        Persona existente = Persona.builder()
                .uniqueId(9L)
                .personaId(new BigDecimal("1234567"))
                .documentoId(1)
                .apellido("LOPEZ")
                .nombre("Maria")
                .build();
        Persona nuevo = Persona.builder()
                .personaId(new BigDecimal("1234567"))
                .documentoId(1)
                .apellido("fernandez saavedra")
                .nombre("ana sofía")
                .build();
        when(repository.findByUniqueId(9L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Persona.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Persona actualizado = useCase.update(nuevo, 9L);

        assertThat(actualizado.getApellido()).isEqualTo("FERNANDEZ SAAVEDRA");
        assertThat(actualizado.getNombre()).isEqualTo("Ana Sofía");
    }
}
