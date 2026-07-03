package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.application.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.model.LectivoTotalImputacion;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.ports.in.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LectivoTotalImputacionServiceTest {

    @Mock
    private FindAllByTipoUseCase findAllByTipoUseCase;
    @Mock
    private FindAllByLectivoUseCase findAllByLectivoUseCase;
    @Mock
    private FindByProductoUseCase findByProductoUseCase;
    @Mock
    private AddLectivoTotalImputacionUseCase addLectivoTotalImputacionUseCase;
    @Mock
    private UpdateLectivoTotalImputacionUseCase updateLectivoTotalImputacionUseCase;

    @InjectMocks
    private LectivoTotalImputacionService service;

    @Test
    void findAllByTipo_delegatesToUseCase() {
        var results = List.of(LectivoTotalImputacion.builder().build());
        when(findAllByTipoUseCase.findAllByTipo(1, 2, 3)).thenReturn(results);

        assertThat(service.findAllByTipo(1, 2, 3)).isEqualTo(results);
        verify(findAllByTipoUseCase).findAllByTipo(1, 2, 3);
    }

    @Test
    void findAllByLectivo_delegatesToUseCase() {
        var results = List.of(LectivoTotalImputacion.builder().build());
        when(findAllByLectivoUseCase.findAllByLectivo(1)).thenReturn(results);

        assertThat(service.findAllByLectivo(1)).isEqualTo(results);
        verify(findAllByLectivoUseCase).findAllByLectivo(1);
    }

    @Test
    void findByProducto_delegatesToUseCase() {
        var domain = LectivoTotalImputacion.builder().build();
        when(findByProductoUseCase.findByProducto(1, 2, 3, 4)).thenReturn(Optional.of(domain));

        assertThat(service.findByProducto(1, 2, 3, 4)).contains(domain);
        verify(findByProductoUseCase).findByProducto(1, 2, 3, 4);
    }

    @Test
    void add_delegatesToUseCase() {
        var domain = LectivoTotalImputacion.builder().build();
        when(addLectivoTotalImputacionUseCase.add(domain)).thenReturn(domain);

        assertThat(service.add(domain)).isEqualTo(domain);
        verify(addLectivoTotalImputacionUseCase).add(domain);
    }

    @Test
    void update_delegatesToUseCase() {
        var domain = LectivoTotalImputacion.builder().build();
        when(updateLectivoTotalImputacionUseCase.update(1L, domain)).thenReturn(Optional.of(domain));

        assertThat(service.update(1L, domain)).contains(domain);
        verify(updateLectivoTotalImputacionUseCase).update(1L, domain);
    }

}
