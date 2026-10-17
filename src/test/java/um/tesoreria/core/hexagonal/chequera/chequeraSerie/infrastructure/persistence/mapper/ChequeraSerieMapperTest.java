package um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.persistence.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.infrastructure.persistence.mapper.ArancelTipoMapper;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.persistence.entity.ChequeraSerieEntity;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.persistence.mapper.TipoChequeraMapper;
import um.tesoreria.core.hexagonal.dependencias.facultad.infrastructure.persistence.mapper.FacultadMapper;
import um.tesoreria.core.hexagonal.dependencias.geografica.infrastructure.persistence.mapper.GeograficaMapper;
import um.tesoreria.core.hexagonal.lectivo.infrastructure.persistence.mapper.LectivoMapper;
import um.tesoreria.core.hexagonal.personas.domicilio.infrastructure.persistence.mapper.DomicilioMapper;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.persistence.mapper.PersonaMapper;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ChequeraSerieMapperTest {

    private final ChequeraSerieMapper mapper = new ChequeraSerieMapper(
            mock(ArancelTipoMapper.class),
            mock(DomicilioMapper.class),
            mock(FacultadMapper.class),
            mock(GeograficaMapper.class),
            mock(LectivoMapper.class),
            mock(PersonaMapper.class),
            mock(TipoChequeraMapper.class));

    /**
     * Regresión: el endpoint `.../asignaciones` usa `toEstadoUsuarioDomain`, que omitía `hpum` y
     * dejaba la insignia HPUM de externo-consulta en falso aunque `chequera_serie.hpum` fuera 1.
     */
    @Test
    void toEstadoUsuarioDomainPropagaHpumMarcado() {
        var entity = entity().hpum((byte) 1).build();

        var domain = mapper.toEstadoUsuarioDomain(entity);

        assertThat(domain.getHpum()).isEqualTo((byte) 1);
    }

    @Test
    void toEstadoUsuarioDomainPropagaHpumSinMarca() {
        var entity = entity().hpum((byte) 0).build();

        var domain = mapper.toEstadoUsuarioDomain(entity);

        assertThat(domain.getHpum()).isEqualTo((byte) 0);
    }

    @Test
    void toEstadoUsuarioDomainUsaCeroCuandoHpumEsNulo() {
        var entity = entity().hpum(null).build();

        var domain = mapper.toEstadoUsuarioDomain(entity);

        assertThat(domain.getHpum()).isEqualTo((byte) 0);
    }

    @Test
    void toEstadoUsuarioDomainNullDevuelveNull() {
        assertThat(mapper.toEstadoUsuarioDomain(null)).isNull();
    }

    private ChequeraSerieEntity.ChequeraSerieEntityBuilder entity() {
        return ChequeraSerieEntity.builder()
                .chequeraId(331642L)
                .facultadId(15)
                .tipoChequeraId(400)
                .chequeraSerieId(1668L)
                .personaId(new BigDecimal("49083260"))
                .documentoId(1)
                .lectivoId(37)
                .geograficaId(1)
                .alternativaId(1);
    }

}
