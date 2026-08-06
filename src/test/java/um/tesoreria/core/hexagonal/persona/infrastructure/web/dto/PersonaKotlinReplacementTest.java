package um.tesoreria.core.hexagonal.persona.infrastructure.web.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto.DomicilioDto;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto.PersonaDto;

class PersonaKotlinReplacementTest {

    @Test
    void personaDto_preservesKotlinDefaultsAndMutableProperties() {
        var dto = new PersonaDto();

        assertThat(dto.getPersonaId()).isNull();
        assertThat(dto.getDocumentoId()).isNull();
        assertThat(dto.getApellido()).isEmpty();
        assertThat(dto.getNombre()).isEmpty();

        dto.setPersonaId(new BigDecimal("12.34"));
        dto.setDocumentoId(7);
        dto.setApellido("Perez");
        dto.setNombre("Ana");

        assertThat(dto).isEqualTo(PersonaDto.builder()
                .personaId(new BigDecimal("12.34"))
                .documentoId(7)
                .apellido("Perez")
                .nombre("Ana")
                .build());
    }

    @Test
    void domicilioDto_preservesKotlinDefaultsAndMutableProperties() {
        var dto = new DomicilioDto();

        assertThat(dto.getEmailPersonal()).isEmpty();
        assertThat(dto.getEmailInstitucional()).isEmpty();

        dto.setEmailPersonal("personal@example.test");
        dto.setEmailInstitucional("institutional@example.test");

        assertThat(dto.getEmailPersonal()).isEqualTo("personal@example.test");
        assertThat(dto.getEmailInstitucional()).isEqualTo("institutional@example.test");
    }
}
