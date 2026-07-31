package um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.infrastructure.web.dto.TesoreriaEstadoFacultadResponse;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TesoreriaEstadoFacultadMapperTest {

    private final TesoreriaEstadoFacultadMapper mapper = new TesoreriaEstadoFacultadMapper();

    @Test
    void toDomain_whenResponseIsNull_returnsNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    void toDomain_mapsAllFields() {
        var response = TesoreriaEstadoFacultadResponse.builder()
                .tesoreriaEstadoId(10L)
                .facultadId(1)
                .personaId(new BigDecimal("1001"))
                .documentoId(2)
                .deuda(new BigDecimal("1500.50"))
                .manual((byte) 1)
                .importado((byte) 1)
                .observaciones("obs")
                .fechaTope(OffsetDateTime.parse("2026-08-01T00:00:00Z"))
                .uuid("abc")
                .build();

        var domain = mapper.toDomain(response);

        assertThat(domain.getTesoreriaEstadoId()).isEqualTo(10L);
        assertThat(domain.getFacultadId()).isEqualTo(1);
        assertThat(domain.getPersonaId()).isEqualByComparingTo(new BigDecimal("1001"));
        assertThat(domain.getDocumentoId()).isEqualTo(2);
        assertThat(domain.getDeuda()).isEqualByComparingTo(new BigDecimal("1500.50"));
        assertThat(domain.getManual()).isEqualTo((byte) 1);
        assertThat(domain.getImportado()).isEqualTo((byte) 1);
        assertThat(domain.getObservaciones()).isEqualTo("obs");
        assertThat(domain.getFechaTope()).isEqualTo(OffsetDateTime.parse("2026-08-01T00:00:00Z"));
        assertThat(domain.getUuid()).isEqualTo("abc");
    }

    @Test
    void toDomain_nullBuilderDefaultFields_applyDomainDefaults() {
        var response = TesoreriaEstadoFacultadResponse.builder()
                .tesoreriaEstadoId(10L)
                .facultadId(1)
                .personaId(new BigDecimal("1001"))
                .documentoId(2)
                .build();

        var domain = mapper.toDomain(response);

        assertThat(domain.getManual()).isEqualTo((byte) 0);
        assertThat(domain.getImportado()).isEqualTo((byte) 0);
        assertThat(domain.getObservaciones()).isEmpty();
        assertThat(domain.getUuid()).isEmpty();
    }
}
