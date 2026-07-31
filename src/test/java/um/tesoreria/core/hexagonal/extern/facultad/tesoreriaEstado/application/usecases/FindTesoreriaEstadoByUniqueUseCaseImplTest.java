package um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.domain.model.TesoreriaEstadoFacultad;
import um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.domain.ports.out.TesoreriaEstadoRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindTesoreriaEstadoByUniqueUseCaseImplTest {

    @Mock
    private TesoreriaEstadoRepository repository;

    @InjectMocks
    private FindTesoreriaEstadoByUniqueUseCaseImpl useCase;

    @Test
    void findByUnique_delegatesToRepository() {
        var domain = TesoreriaEstadoFacultad.builder()
                .facultadId(1)
                .personaId(new BigDecimal("1001"))
                .documentoId(2)
                .build();

        when(repository.findByUnique(1, new BigDecimal("1001"), 2)).thenReturn(Optional.of(domain));

        var result = useCase.findByUnique(1, new BigDecimal("1001"), 2);

        assertThat(result).contains(domain);
        verify(repository).findByUnique(1, new BigDecimal("1001"), 2);
    }

    @Test
    void findByUnique_whenNotFound_returnsEmpty() {
        when(repository.findByUnique(1, new BigDecimal("1001"), 2)).thenReturn(Optional.empty());

        var result = useCase.findByUnique(1, new BigDecimal("1001"), 2);

        assertThat(result).isEmpty();
        verify(repository).findByUnique(1, new BigDecimal("1001"), 2);
    }
}
