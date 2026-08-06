package um.tesoreria.core.hexagonal.persona.infrastructure.persistence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import um.tesoreria.core.hexagonal.personas.persona.infrastructure.persistence.entity.PersonaKeyEntity;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.persistence.mapper.PersonaKeyMapper;

class PersonaKeyMapperTest {

    private final PersonaKeyMapper mapper = new PersonaKeyMapper();

    @Test
    void toDomain_mapsAllPersonaKeyFields() {
        var entity = new PersonaKeyEntity(
                "1.2.3.4",
                99L,
                new BigDecimal("123.45"),
                8,
                "Perez",
                "Ana",
                "F",
                (byte) 1,
                "20-12345678-9",
                "CBU",
                "secret",
                "perez ana",
                true);

        var domain = mapper.toDomain(entity);

        assertThat(domain.getUnified()).isEqualTo("1.2.3.4");
        assertThat(domain.getUniqueId()).isEqualTo(99L);
        assertThat(domain.getPersonaId()).isEqualByComparingTo("123.45");
        assertThat(domain.getDocumentoId()).isEqualTo(8);
        assertThat(domain.getApellido()).isEqualTo("Perez");
        assertThat(domain.getNombre()).isEqualTo("Ana");
        assertThat(domain.getSexo()).isEqualTo("F");
        assertThat(domain.getPrimero()).isEqualTo((byte) 1);
        assertThat(domain.getCuit()).isEqualTo("20-12345678-9");
        assertThat(domain.getCbu()).isEqualTo("CBU");
        assertThat(domain.getPassword()).isEqualTo("secret");
        assertThat(domain.getSearch()).isEqualTo("perez ana");
        assertThat(domain.getMark_facultad()).isTrue();
    }

    @Test
    void toDomain_whenEntityIsNull_returnsNull() {
        assertThat(mapper.toDomain(null)).isNull();
    }
}
