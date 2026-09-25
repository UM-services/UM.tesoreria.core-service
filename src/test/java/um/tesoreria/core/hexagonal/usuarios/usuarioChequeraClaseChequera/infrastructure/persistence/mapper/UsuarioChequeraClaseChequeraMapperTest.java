package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.persistence.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.model.ClaseChequera;
import um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.persistence.entity.ClaseChequeraEntity;
import um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.persistence.mapper.ClaseChequeraMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.model.UsuarioChequeraClaseChequera;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.persistence.entity.UsuarioChequeraClaseChequeraEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioChequeraClaseChequeraMapperTest {

    @Mock
    private ClaseChequeraMapper claseChequeraMapper;

    @InjectMocks
    private UsuarioChequeraClaseChequeraMapper mapper;

    @Test
    void toDomain_whenEntityIsNull_returnsNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    void toDomain_mapsScalarFields() {
        var entity = UsuarioChequeraClaseChequeraEntity.builder()
                .usuarioChequeraClaseChequeraId(1L)
                .userId(10L)
                .claseChequeraId(20)
                .build();

        var domain = mapper.toDomain(entity);

        assertThat(domain.getUsuarioChequeraClaseChequeraId()).isEqualTo(1L);
        assertThat(domain.getUserId()).isEqualTo(10L);
        assertThat(domain.getClaseChequeraId()).isEqualTo(20);
    }

    @Test
    void toDomain_propagatesClaseChequera() {
        var claseChequeraEntity = new ClaseChequeraEntity();
        var entity = UsuarioChequeraClaseChequeraEntity.builder()
                .usuarioChequeraClaseChequeraId(1L)
                .userId(10L)
                .claseChequeraId(20)
                .claseChequera(claseChequeraEntity)
                .build();

        var claseChequera = new ClaseChequera();
        when(claseChequeraMapper.toDomainModel(claseChequeraEntity)).thenReturn(claseChequera);

        var domain = mapper.toDomain(entity);

        assertThat(domain.getClaseChequera()).isEqualTo(claseChequera);
    }

    @Test
    void toDomain_whenClaseChequeraIsNull_mapsNullNested() {
        var entity = UsuarioChequeraClaseChequeraEntity.builder()
                .usuarioChequeraClaseChequeraId(1L)
                .claseChequeraId(20)
                .build();

        var domain = mapper.toDomain(entity);

        assertThat(domain.getClaseChequera()).isNull();
    }

    @Test
    void toEntity_whenDomainIsNull_returnsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void toEntity_mapsScalarFieldsAndIgnoresRelation() {
        var domain = UsuarioChequeraClaseChequera.builder()
                .usuarioChequeraClaseChequeraId(1L)
                .userId(10L)
                .claseChequeraId(20)
                .claseChequera(new ClaseChequera())
                .build();

        var entity = mapper.toEntity(domain);

        assertThat(entity.getUsuarioChequeraClaseChequeraId()).isEqualTo(1L);
        assertThat(entity.getUserId()).isEqualTo(10L);
        assertThat(entity.getClaseChequeraId()).isEqualTo(20);
        assertThat(entity.getClaseChequera()).isNull();
    }

}
