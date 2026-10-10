package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.persistence.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JdbcCompraAutoridadUsuarioAdapterTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private JdbcCompraAutoridadUsuarioAdapter adapter;

    @Test
    void findPerfilIdsConsultaYDevuelveLosIds() {
        when(jdbcTemplate.queryForList(anyString(), eq(Long.class), eq(9))).thenReturn(List.of(5L, 6L));

        assertThat(adapter.findPerfilIdsByUsuarioId(9)).containsExactly(5L, 6L);
    }

    @Test
    void asignarEjecutaElInsert() {
        adapter.asignar(9, 5L);

        verify(jdbcTemplate).update(anyString(), eq(9), eq(5L));
    }

    @Test
    void quitarEjecutaElDelete() {
        adapter.quitar(9, 5L);

        verify(jdbcTemplate).update(anyString(), eq(9), eq(5L));
    }

    @Test
    void existeDevuelveTrueCuandoHayFila() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(9), eq(5L))).thenReturn(1);

        assertThat(adapter.existe(9, 5L)).isTrue();
    }

    @Test
    void existeDevuelveFalseCuandoNoHayFila() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(9), eq(5L))).thenReturn(0);

        assertThat(adapter.existe(9, 5L)).isFalse();
    }

}
