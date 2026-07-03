package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.application.usecases;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.model.LectivoTotalImputacion;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.ports.out.LectivoTotalImputacionRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddLectivoTotalImputacionUseCaseImplTest {

    @Mock
    private LectivoTotalImputacionRepository repository;

    @InjectMocks
    private AddLectivoTotalImputacionUseCaseImpl useCase;

    @Test
    void add_delegatesToRepository() {
        var domain = LectivoTotalImputacion.builder()
                .facultadId(1).lectivoId(2).tipoChequeraId(3).productoId(4).build();

        when(repository.add(domain)).thenReturn(domain);

        var result = useCase.add(domain);

        assertThat(result).isEqualTo(domain);
        verify(repository).add(domain);
    }

}
