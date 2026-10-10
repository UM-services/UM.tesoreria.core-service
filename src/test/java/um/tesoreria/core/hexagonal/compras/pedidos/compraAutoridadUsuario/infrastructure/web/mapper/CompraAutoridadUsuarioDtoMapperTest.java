package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.model.LimiteAutorizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.web.dto.CompraAutoridadUsuarioResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.web.dto.LimiteAutorizacionResponse;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CompraAutoridadUsuarioDtoMapperTest {

    private final CompraAutoridadUsuarioDtoMapper mapper = new CompraAutoridadUsuarioDtoMapper();

    @Test
    void toResponseDeLaAsignacion() {
        CompraAutoridadUsuarioResponse response = mapper.toResponse(9, List.of(5L, 6L));

        assertThat(response.getUsuarioId()).isEqualTo(9);
        assertThat(response.getAutoridadPerfilIds()).containsExactly(5L, 6L);
    }

    @Test
    void toResponseDelLimite() {
        LimiteAutorizacionResponse response = mapper.toResponse(new LimiteAutorizacion(
                9, 7, 3, new BigDecimal("1000.00"), new BigDecimal("3000.00"), false, true));

        assertThat(response.getUsuarioId()).isEqualTo(9);
        assertThat(response.getMultiplico()).isEqualTo(3);
        assertThat(response.getLimite()).isEqualByComparingTo("3000.00");
        assertThat(response.getIlimitado()).isFalse();
        assertThat(response.getTieneAutoridad()).isTrue();
    }

    @Test
    void toResponseDelLimiteNulo() {
        assertThat(mapper.toResponse((LimiteAutorizacion) null)).isNull();
    }

}
