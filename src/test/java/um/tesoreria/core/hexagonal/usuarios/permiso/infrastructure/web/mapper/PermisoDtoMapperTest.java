package um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.dto.PermisoRequest;

import static org.assertj.core.api.Assertions.assertThat;

class PermisoDtoMapperTest {

    private final PermisoDtoMapper mapper = new PermisoDtoMapper();

    @Test
    void toResponse_whenNull_returnsNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }

    @Test
    void toResponse_mapsFields() {
        var domain = Permiso.builder()
                .permisoId(1L)
                .clave("chequeras.eliminar")
                .descripcion("Eliminar chequeras")
                .modulo("chequeras")
                .build();

        var response = mapper.toResponse(domain);

        assertThat(response.getPermisoId()).isEqualTo(1L);
        assertThat(response.getClave()).isEqualTo("chequeras.eliminar");
        assertThat(response.getModulo()).isEqualTo("chequeras");
        assertThat(response.getAplicacion()).isEqualTo("TESORERIA");
    }

    @Test
    void toDomain_whenNull_returnsNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    void toDomain_mapsRequestAndAppliesDefaults() {
        var request = PermisoRequest.builder()
                .clave("pagos.reembolsos")
                .descripcion("Reembolsos")
                .modulo("pagos")
                .build();

        var domain = mapper.toDomain(request);

        assertThat(domain.getClave()).isEqualTo("pagos.reembolsos");
        assertThat(domain.getDescripcion()).isEqualTo("Reembolsos");
        assertThat(domain.getModulo()).isEqualTo("pagos");
        assertThat(domain.getAplicacion()).isEqualTo("TESORERIA");
        assertThat(domain.getActivo()).isEqualTo((byte) 1);
    }

    @Test
    void toDomain_whenAplicacionAndActivoPresent_usesThem() {
        var request = PermisoRequest.builder()
                .clave("pagos.reembolsos")
                .descripcion("Reembolsos")
                .modulo("pagos")
                .aplicacion("HABERES")
                .activo((byte) 0)
                .build();

        var domain = mapper.toDomain(request);

        assertThat(domain.getAplicacion()).isEqualTo("HABERES");
        assertThat(domain.getActivo()).isEqualTo((byte) 0);
    }
}
