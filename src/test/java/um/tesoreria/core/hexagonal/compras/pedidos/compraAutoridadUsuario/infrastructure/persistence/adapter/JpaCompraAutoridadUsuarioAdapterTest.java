package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.persistence.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.persistence.entity.CompraAutoridadUsuarioEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.persistence.repository.JpaCompraAutoridadUsuarioRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaCompraAutoridadUsuarioAdapterTest {

    @Mock
    private JpaCompraAutoridadUsuarioRepository repository;

    @InjectMocks
    private JpaCompraAutoridadUsuarioAdapter adapter;

    @Test
    void findPerfilIdsDevuelveLosIdsOrdenados() {
        when(repository.findAllByUsuarioIdOrderByAutoridadPerfilIdAsc(9))
                .thenReturn(List.of(new CompraAutoridadUsuarioEntity(9, 5L),
                        new CompraAutoridadUsuarioEntity(9, 6L)));

        assertThat(adapter.findPerfilIdsByUsuarioId(9)).containsExactly(5L, 6L);
    }

    @Test
    void asignarGuardaCuandoNoExiste() {
        when(repository.existsByUsuarioIdAndAutoridadPerfilId(9, 5L)).thenReturn(false);

        adapter.asignar(9, 5L);

        ArgumentCaptor<CompraAutoridadUsuarioEntity> captor =
                ArgumentCaptor.forClass(CompraAutoridadUsuarioEntity.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getUsuarioId()).isEqualTo(9);
        assertThat(captor.getValue().getAutoridadPerfilId()).isEqualTo(5L);
    }

    @Test
    void asignarEsIdempotenteYNoGuardaSiYaExiste() {
        when(repository.existsByUsuarioIdAndAutoridadPerfilId(9, 5L)).thenReturn(true);

        adapter.asignar(9, 5L);

        verify(repository, never()).save(any());
    }

    @Test
    void quitarBorraPorUsuarioYPerfil() {
        adapter.quitar(9, 5L);

        verify(repository).deleteByUsuarioIdAndAutoridadPerfilId(9, 5L);
    }

    @Test
    void existeDelegaEnElRepositorio() {
        when(repository.existsByUsuarioIdAndAutoridadPerfilId(9, 5L)).thenReturn(true);

        assertThat(adapter.existe(9, 5L)).isTrue();
    }

}
