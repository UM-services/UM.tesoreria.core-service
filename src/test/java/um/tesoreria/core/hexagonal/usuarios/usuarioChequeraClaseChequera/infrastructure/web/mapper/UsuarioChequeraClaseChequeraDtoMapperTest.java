package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.model.ClaseChequera;
import um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.web.mapper.ClaseChequeraDtoMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.model.UsuarioChequeraClaseChequera;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.web.dto.UsuarioChequeraClaseChequeraResponse;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioChequeraClaseChequeraDtoMapperTest {

    private final UsuarioChequeraClaseChequeraDtoMapper mapper =
            new UsuarioChequeraClaseChequeraDtoMapper(new ClaseChequeraDtoMapper());

    @Test
    void toResponse_whenDomainIsNull_returnsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }

    @Test
    void toResponse_mapsScalarFields() {
        var domain = UsuarioChequeraClaseChequera.builder()
                .usuarioChequeraClaseChequeraId(1L)
                .userId(10L)
                .claseChequeraId(20)
                .build();

        var response = mapper.toResponse(domain);

        assertThat(response.getUsuarioChequeraClaseChequeraId()).isEqualTo(1L);
        assertThat(response.getUserId()).isEqualTo(10L);
        assertThat(response.getClaseChequeraId()).isEqualTo(20);
    }

    @Test
    void toResponse_propagatesClaseChequera() {
        var claseChequera = ClaseChequera.builder()
                .claseChequeraId(20)
                .nombre("Planilla")
                .build();
        var domain = UsuarioChequeraClaseChequera.builder()
                .usuarioChequeraClaseChequeraId(1L)
                .userId(10L)
                .claseChequeraId(20)
                .claseChequera(claseChequera)
                .build();

        var response = mapper.toResponse(domain);

        assertThat(response.getClaseChequera()).isNotNull();
        assertThat(response.getClaseChequera().getClaseChequeraId()).isEqualTo(20);
        assertThat(response.getClaseChequera().getNombre()).isEqualTo("Planilla");
    }

    @Test
    void toResponse_whenClaseChequeraIsNull_mapsNullNested() {
        var domain = UsuarioChequeraClaseChequera.builder()
                .usuarioChequeraClaseChequeraId(1L)
                .claseChequeraId(20)
                .build();

        var response = mapper.toResponse(domain);

        assertThat(response.getClaseChequera()).isNull();
    }

}
