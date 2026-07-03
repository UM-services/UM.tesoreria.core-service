package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.domain.model.LectivoTotalImputacion;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.web.dto.LectivoTotalImputacionRequest;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.web.dto.LectivoTotalImputacionResponse;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class LectivoTotalImputacionDtoMapperTest {

    private final LectivoTotalImputacionDtoMapper mapper = new LectivoTotalImputacionDtoMapper();

    @Test
    void toDomain_whenRequestIsNull_returnsNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    void toDomain_mapsAllFields() {
        var request = new LectivoTotalImputacionRequest();
        request.setLectivoTotalImputacionId(1L);
        request.setFacultadId(100);
        request.setLectivoId(200);
        request.setTipoChequeraId(300);
        request.setProductoId(400);
        request.setNumeroCuenta(new BigDecimal("123.45"));

        var domain = mapper.toDomain(request);

        assertThat(domain.getLectivoTotalImputacionId()).isEqualTo(1L);
        assertThat(domain.getFacultadId()).isEqualTo(100);
        assertThat(domain.getLectivoId()).isEqualTo(200);
        assertThat(domain.getTipoChequeraId()).isEqualTo(300);
        assertThat(domain.getProductoId()).isEqualTo(400);
        assertThat(domain.getNumeroCuenta()).isEqualByComparingTo(new BigDecimal("123.45"));
    }

    @Test
    void toResponse_whenDomainIsNull_returnsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }

    @Test
    void toResponse_mapsAllFields() {
        var domain = LectivoTotalImputacion.builder()
                .lectivoTotalImputacionId(1L)
                .facultadId(100)
                .lectivoId(200)
                .tipoChequeraId(300)
                .productoId(400)
                .numeroCuenta(new BigDecimal("123.45"))
                .build();

        var response = mapper.toResponse(domain);

        assertThat(response.getLectivoTotalImputacionId()).isEqualTo(1L);
        assertThat(response.getFacultadId()).isEqualTo(100);
        assertThat(response.getLectivoId()).isEqualTo(200);
        assertThat(response.getTipoChequeraId()).isEqualTo(300);
        assertThat(response.getProductoId()).isEqualTo(400);
        assertThat(response.getNumeroCuenta()).isEqualByComparingTo(new BigDecimal("123.45"));
    }

}
