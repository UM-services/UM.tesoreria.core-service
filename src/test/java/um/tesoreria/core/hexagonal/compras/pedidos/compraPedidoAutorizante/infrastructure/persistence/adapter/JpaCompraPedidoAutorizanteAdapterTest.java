package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.infrastructure.persistence.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.infrastructure.persistence.entity.CompraPedidoAutorizanteDependenciaEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.infrastructure.persistence.repository.JpaCompraPedidoAutorizanteDependenciaRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaCompraPedidoAutorizanteAdapterTest {

    @Mock
    private JpaCompraPedidoAutorizanteDependenciaRepository repository;

    @InjectMocks
    private JpaCompraPedidoAutorizanteAdapter adapter;

    @Test
    void findDependenciaIdsDevuelveLosIdsOrdenados() {
        when(repository.findAllByAutorizanteIdOrderByDependenciaIdAsc(9))
                .thenReturn(List.of(new CompraPedidoAutorizanteDependenciaEntity(9, 1),
                        new CompraPedidoAutorizanteDependenciaEntity(9, 2)));

        assertThat(adapter.findDependenciaIdsByAutorizanteId(9)).containsExactly(1, 2);
    }

    @Test
    void asignarGuardaCuandoNoExiste() {
        when(repository.existsByAutorizanteIdAndDependenciaId(9, 2)).thenReturn(false);

        adapter.asignar(9, 2);

        ArgumentCaptor<CompraPedidoAutorizanteDependenciaEntity> captor =
                ArgumentCaptor.forClass(CompraPedidoAutorizanteDependenciaEntity.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getAutorizanteId()).isEqualTo(9);
        assertThat(captor.getValue().getDependenciaId()).isEqualTo(2);
    }

    @Test
    void asignarEsIdempotenteYNoGuardaSiYaExiste() {
        when(repository.existsByAutorizanteIdAndDependenciaId(9, 2)).thenReturn(true);

        adapter.asignar(9, 2);

        verify(repository, never()).save(any());
    }

    @Test
    void quitarBorraPorAutorizanteYDependencia() {
        adapter.quitar(9, 2);

        verify(repository).deleteByAutorizanteIdAndDependenciaId(9, 2);
    }

    @Test
    void existeDelegaEnElRepositorio() {
        when(repository.existsByAutorizanteIdAndDependenciaId(9, 2)).thenReturn(true);

        assertThat(adapter.existe(9, 2)).isTrue();
    }

}
