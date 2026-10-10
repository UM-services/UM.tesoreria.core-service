package um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.model.CompraReferencia;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.web.dto.CompraReferenciaRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.web.dto.CompraReferenciaResponse;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class CompraReferenciaDtoMapperTest {

    private final CompraReferenciaDtoMapper mapper = new CompraReferenciaDtoMapper();

    @Test
    void toDomainUsaElEjercicioDelPath() {
        CompraReferencia domain = mapper.toDomain(7, CompraReferenciaRequest.builder()
                .importe(new BigDecimal("100.00")).build());

        assertThat(domain.getEjercicioId()).isEqualTo(7);
        assertThat(domain.getImporte()).isEqualByComparingTo("100.00");
    }

    @Test
    void toResponseCopiaLosCampos() {
        CompraReferenciaResponse response = mapper.toResponse(CompraReferencia.builder()
                .ejercicioId(7).importe(new BigDecimal("100.00")).build());

        assertThat(response.getEjercicioId()).isEqualTo(7);
        assertThat(response.getImporte()).isEqualByComparingTo("100.00");
    }

    @Test
    void mapeaNulos() {
        assertThat(mapper.toDomain(7, null)).isNull();
        assertThat(mapper.toResponse(null)).isNull();
    }

}
