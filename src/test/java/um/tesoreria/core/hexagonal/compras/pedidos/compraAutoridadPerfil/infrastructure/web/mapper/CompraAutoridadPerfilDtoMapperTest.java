package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.model.CompraAutoridadPerfil;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.web.dto.CompraAutoridadPerfilRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.web.dto.CompraAutoridadPerfilResponse;

import static org.assertj.core.api.Assertions.assertThat;

class CompraAutoridadPerfilDtoMapperTest {

    private final CompraAutoridadPerfilDtoMapper mapper = new CompraAutoridadPerfilDtoMapper();

    @Test
    void toDomainCopiaCampos() {
        CompraAutoridadPerfil domain = mapper.toDomain(CompraAutoridadPerfilRequest.builder()
                .nombre("N1").multiplico(3).activo((byte) 1).build());

        assertThat(domain.getNombre()).isEqualTo("N1");
        assertThat(domain.getMultiplico()).isEqualTo(3);
    }

    @Test
    void toResponseMarcaIlimitadoCuandoMultiplicoEsNulo() {
        CompraAutoridadPerfilResponse conLimite = mapper.toResponse(
                CompraAutoridadPerfil.builder().nombre("N1").multiplico(3).build());
        CompraAutoridadPerfilResponse ilimitado = mapper.toResponse(
                CompraAutoridadPerfil.builder().nombre("Ilimitado").multiplico(null).build());

        assertThat(conLimite.getIlimitado()).isFalse();
        assertThat(ilimitado.getIlimitado()).isTrue();
    }

    @Test
    void mapeaNulos() {
        assertThat(mapper.toDomain(null)).isNull();
        assertThat(mapper.toResponse(null)).isNull();
    }

}
