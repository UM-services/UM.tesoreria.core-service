package um.tesoreria.core.hexagonal.usuarios.usuarioRol.infrastructure.persistence.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.persistence.entity.RolEntity;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.persistence.mapper.RolMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.model.UsuarioRol;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.infrastructure.persistence.entity.UsuarioRolEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioRolMapperTest {

    @Mock
    private RolMapper rolMapper;

    @InjectMocks
    private UsuarioRolMapper mapper;

    @Test
    void toDomain_mapsScalarsAndNested() {
        var rolEntity = new RolEntity();
        var entity = UsuarioRolEntity.builder().usuarioRolId(1L).userId(1L).rolId(5L).rol(rolEntity).build();
        when(rolMapper.toDomain(rolEntity)).thenReturn(Rol.builder().rolId(5L).build());

        var domain = mapper.toDomain(entity);

        assertThat(domain.getUserId()).isEqualTo(1L);
        assertThat(domain.getRol().getRolId()).isEqualTo(5L);
    }

    @Test
    void toEntity_ignoresRelation() {
        var entity = mapper.toEntity(UsuarioRol.builder().userId(1L).rolId(5L).rol(Rol.builder().build()).build());

        assertThat(entity.getUserId()).isEqualTo(1L);
        assertThat(entity.getRolId()).isEqualTo(5L);
        assertThat(entity.getRol()).isNull();
    }
}
