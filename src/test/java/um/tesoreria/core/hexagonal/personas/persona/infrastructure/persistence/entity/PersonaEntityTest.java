package um.tesoreria.core.hexagonal.personas.persona.infrastructure.persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PersonaEntityTest {

    @Test
    void builder_appliesDeclaredDefaults() {
        var entity = PersonaEntity.builder().build();

        assertThat(entity.getNumeroPrefijo()).isEmpty();
        assertThat(entity.getNumeroPosfijo()).isEmpty();
        assertThat(entity.getPrimero()).isEqualTo((byte) 0);
        assertThat(entity.getCuit()).isEmpty();
        assertThat(entity.getCbu()).isEmpty();
    }

    @Test
    void builder_allowsOverridingDefaults() {
        var entity = PersonaEntity.builder()
                .primero((byte) 1)
                .cuit("20-12345678-9")
                .cbu("0000000000000000000000")
                .build();

        assertThat(entity.getPrimero()).isEqualTo((byte) 1);
        assertThat(entity.getCuit()).isEqualTo("20-12345678-9");
        assertThat(entity.getCbu()).isEqualTo("0000000000000000000000");
    }

}
