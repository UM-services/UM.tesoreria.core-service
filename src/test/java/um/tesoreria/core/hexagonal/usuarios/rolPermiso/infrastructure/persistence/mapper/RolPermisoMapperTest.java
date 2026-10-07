package um.tesoreria.core.hexagonal.usuarios.rolPermiso.infrastructure.persistence.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.persistence.entity.PermisoEntity;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.persistence.mapper.PermisoMapper;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.persistence.entity.RolEntity;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.persistence.mapper.RolMapper;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.model.RolPermiso;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.infrastructure.persistence.entity.RolPermisoEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolPermisoMapperTest {

    @Mock
    private RolMapper rolMapper;
    @Mock
    private PermisoMapper permisoMapper;

    @InjectMocks
    private RolPermisoMapper mapper;

    @Test
    void toDomain_whenNull_returnsNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    void toDomain_mapsScalarsAndNested() {
        var rolEntity = new RolEntity();
        var permisoEntity = new PermisoEntity();
        var entity = RolPermisoEntity.builder()
                .rolPermisoId(1L).rolId(5L).permisoId(10L)
                .rol(rolEntity).permiso(permisoEntity).build();
        when(rolMapper.toDomain(rolEntity)).thenReturn(Rol.builder().rolId(5L).build());
        when(permisoMapper.toDomain(permisoEntity)).thenReturn(Permiso.builder().permisoId(10L).build());

        var domain = mapper.toDomain(entity);

        assertThat(domain.getRolPermisoId()).isEqualTo(1L);
        assertThat(domain.getRol().getRolId()).isEqualTo(5L);
        assertThat(domain.getPermiso().getPermisoId()).isEqualTo(10L);
    }

    @Test
    void toEntity_mapsScalarsAndIgnoresRelations() {
        var domain = RolPermiso.builder()
                .rolPermisoId(1L).rolId(5L).permisoId(10L)
                .rol(Rol.builder().build()).permiso(Permiso.builder().build()).build();

        var entity = mapper.toEntity(domain);

        assertThat(entity.getRolId()).isEqualTo(5L);
        assertThat(entity.getPermisoId()).isEqualTo(10L);
        assertThat(entity.getRol()).isNull();
        assertThat(entity.getPermiso()).isNull();
    }
}
