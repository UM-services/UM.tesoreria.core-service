package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.infrastructure.persistence.adapter;

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
class JdbcCompraPedidoAutorizanteAdapterTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private JdbcCompraPedidoAutorizanteAdapter adapter;

    @Test
    void findDependenciaIdsByAutorizanteIdConsultaYDevuelveLosIds() {
        when(jdbcTemplate.queryForList(anyString(), eq(Integer.class), eq(9))).thenReturn(List.of(1, 2));

        assertThat(adapter.findDependenciaIdsByAutorizanteId(9)).containsExactly(1, 2);
    }

    @Test
    void asignarEjecutaElInsert() {
        adapter.asignar(9, 2);

        verify(jdbcTemplate).update(anyString(), eq(9), eq(2));
    }

    @Test
    void quitarEjecutaElDelete() {
        adapter.quitar(9, 2);

        verify(jdbcTemplate).update(anyString(), eq(9), eq(2));
    }

    @Test
    void existeDevuelveTrueCuandoHayFila() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(9), eq(2))).thenReturn(1);

        assertThat(adapter.existe(9, 2)).isTrue();
    }

    @Test
    void existeDevuelveFalseCuandoNoHayFila() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(9), eq(2))).thenReturn(0);

        assertThat(adapter.existe(9, 2)).isFalse();
    }

}
