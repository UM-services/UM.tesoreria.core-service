package um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.infrastructure.persistence.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.persistence.entity.PermisoEntity;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.persistence.mapper.PermisoMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.model.UsuarioPermiso;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.infrastructure.persistence.entity.UsuarioPermisoEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioPermisoMapperTest {

    @Mock
    private PermisoMapper permisoMapper;

    @InjectMocks
    private UsuarioPermisoMapper mapper;

    @Test
    void toDomain_mapsScalarsAndNested() {
        var permisoEntity = new PermisoEntity();
        var entity = UsuarioPermisoEntity.builder()
                .usuarioPermisoId(1L).userId(1L).permisoId(10L).otorgado((byte) 0).permiso(permisoEntity).build();
        when(permisoMapper.toDomain(permisoEntity)).thenReturn(Permiso.builder().clave("chequeras.eliminar").build());

        var domain = mapper.toDomain(entity);

        assertThat(domain.getOtorgado()).isEqualTo((byte) 0);
        assertThat(domain.getPermiso().getClave()).isEqualTo("chequeras.eliminar");
    }

    @Test
    void toEntity_ignoresRelation() {
        var entity = mapper.toEntity(UsuarioPermiso.builder()
                .userId(1L).permisoId(10L).otorgado((byte) 1).permiso(Permiso.builder().build()).build());

        assertThat(entity.getUserId()).isEqualTo(1L);
        assertThat(entity.getOtorgado()).isEqualTo((byte) 1);
        assertThat(entity.getPermiso()).isNull();
    }
}
