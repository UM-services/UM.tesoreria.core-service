package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.persistence.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import um.tesoreria.core.hexagonal.dependencias.geografica.domain.model.Geografica;
import um.tesoreria.core.hexagonal.dependencias.geografica.infrastructure.persistence.entity.GeograficaEntity;
import um.tesoreria.core.hexagonal.dependencias.geografica.infrastructure.persistence.mapper.GeograficaMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.model.UsuarioChequeraGeografica;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.persistence.entity.UsuarioChequeraGeograficaEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioChequeraGeograficaMapperTest {

    @Mock
    private GeograficaMapper geograficaMapper;

    @InjectMocks
    private UsuarioChequeraGeograficaMapper mapper;

    @Test
    void toDomain_whenEntityIsNull_returnsNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    void toDomain_mapsScalarFields() {
        var entity = UsuarioChequeraGeograficaEntity.builder()
                .usuarioChequeraGeograficaId(1L)
                .userId(10L)
                .geograficaId(20)
                .build();

        var domain = mapper.toDomain(entity);

        assertThat(domain.getUsuarioChequeraGeograficaId()).isEqualTo(1L);
        assertThat(domain.getUserId()).isEqualTo(10L);
        assertThat(domain.getGeograficaId()).isEqualTo(20);
    }

    @Test
    void toDomain_propagatesGeografica() {
        var geograficaEntity = new GeograficaEntity();
        var entity = UsuarioChequeraGeograficaEntity.builder()
                .usuarioChequeraGeograficaId(1L)
                .userId(10L)
                .geograficaId(20)
                .geografica(geograficaEntity)
                .build();

        var geografica = new Geografica();
        when(geograficaMapper.toDomainModel(geograficaEntity)).thenReturn(geografica);

        var domain = mapper.toDomain(entity);

        assertThat(domain.getGeografica()).isEqualTo(geografica);
    }

    @Test
    void toDomain_whenGeograficaIsNull_mapsNullNested() {
        var entity = UsuarioChequeraGeograficaEntity.builder()
                .usuarioChequeraGeograficaId(1L)
                .geograficaId(20)
                .build();

        var domain = mapper.toDomain(entity);

        assertThat(domain.getGeografica()).isNull();
    }

    @Test
    void toEntity_whenDomainIsNull_returnsNull() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void toEntity_mapsScalarFieldsAndIgnoresRelation() {
        var domain = UsuarioChequeraGeografica.builder()
                .usuarioChequeraGeograficaId(1L)
                .userId(10L)
                .geograficaId(20)
                .geografica(new Geografica())
                .build();

        var entity = mapper.toEntity(domain);

        assertThat(entity.getUsuarioChequeraGeograficaId()).isEqualTo(1L);
        assertThat(entity.getUserId()).isEqualTo(10L);
        assertThat(entity.getGeograficaId()).isEqualTo(20);
        assertThat(entity.getGeografica()).isNull();
    }

}
