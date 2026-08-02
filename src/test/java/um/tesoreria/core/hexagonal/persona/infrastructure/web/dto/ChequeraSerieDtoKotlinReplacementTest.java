package um.tesoreria.core.hexagonal.persona.infrastructure.web.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.web.dto.ChequeraSerieDto;

class ChequeraSerieDtoKotlinReplacementTest {

    @Test
    void preservesKotlinDefaultValues() {
        var dto = new ChequeraSerieDto();

        assertThat(dto.getChequeraSerieId()).isNull();
        assertThat(dto.getFecha()).isNull();
        assertThat(dto.getObservaciones()).isEmpty();
        assertThat(dto.getAlternativaId()).isZero();
        assertThat(dto.getFacultad()).isNull();
        assertThat(dto.getTipoChequera()).isNull();
        assertThat(dto.getPersona()).isNull();
        assertThat(dto.getMails()).isNull();
        assertThat(dto.getChequeraCuotas()).isNull();
    }
}
