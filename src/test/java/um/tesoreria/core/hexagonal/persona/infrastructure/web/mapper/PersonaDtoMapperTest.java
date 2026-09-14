package um.tesoreria.core.hexagonal.persona.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto.PersonaRequest;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto.PersonaResponse;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.mapper.PersonaDtoMapper;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PersonaDtoMapperTest {

    private final PersonaDtoMapper mapper = new PersonaDtoMapper();

    @Test
    void roundTrip_requestDomainResponse_preservaPrefijoPosfijoYGuaraniPersona() {
        PersonaRequest request = PersonaRequest.builder()
                .uniqueId(5L)
                .personaId(new BigDecimal("1234567"))
                .documentoId(2)
                .apellido("García")
                .nombre("Juan")
                .sexo("M")
                .primero((byte) 1)
                .cuit("")
                .cbu("")
                .password("")
                .hpum((byte) 0)
                .numeroPrefijo("AA")
                .numeroPosfijo("B")
                .guaraniPersona(4500L)
                .build();

        Persona domain = mapper.toDomain(request);
        PersonaResponse response = mapper.toResponse(domain);

        assertThat(domain.getNumeroPrefijo()).isEqualTo("AA");
        assertThat(domain.getNumeroPosfijo()).isEqualTo("B");
        assertThat(domain.getGuaraniPersona()).isEqualTo(4500L);

        assertThat(response.getUniqueId()).isEqualTo(5L);
        assertThat(response.getPersonaId()).isEqualByComparingTo("1234567");
        assertThat(response.getDocumentoId()).isEqualTo(2);
        assertThat(response.getPrimero()).isEqualTo((byte) 1);
        assertThat(response.getHpum()).isEqualTo((byte) 0);
        assertThat(response.getNumeroPrefijo()).isEqualTo("AA");
        assertThat(response.getNumeroPosfijo()).isEqualTo("B");
        assertThat(response.getGuaraniPersona()).isEqualTo(4500L);
    }

    @Test
    void toResponse_personaCreadaSinPrefijoPosfijo_devuelveVacioYEcoGuaraniPersonaNull() {
        Persona domain = Persona.builder()
                .uniqueId(9L)
                .personaId(new BigDecimal("7654321"))
                .documentoId(2)
                .build();

        PersonaResponse response = mapper.toResponse(domain);

        assertThat(domain.getNumeroPrefijo()).isEmpty();
        assertThat(domain.getNumeroPosfijo()).isEmpty();
        assertThat(response.getNumeroPrefijo()).isEmpty();
        assertThat(response.getNumeroPosfijo()).isEmpty();
        assertThat(response.getGuaraniPersona()).isNull();
    }

}
