package um.tesoreria.core.hexagonal.personas.legajo.infrastructure.persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LegajoEntityTest {

    @Test
    void builder_appliesDeclaredDefaults() {
        var entity = LegajoEntity.builder().build();

        assertThat(entity.getNumeroLegajo()).isEqualTo(0L);
        assertThat(entity.getTieneCarrera()).isEqualTo((byte) 0);
        assertThat(entity.getIntercambio()).isEqualTo((byte) 0);
        assertThat(entity.getLegajoId()).isNull();
        assertThat(entity.getPersonaId()).isNull();
        assertThat(entity.getCarrera()).isNull();
    }

    @Test
    void builder_allowsOverridingDefaults() {
        var entity = LegajoEntity.builder()
                .numeroLegajo(15L)
                .tieneCarrera((byte) 1)
                .intercambio((byte) 1)
                .build();

        assertThat(entity.getNumeroLegajo()).isEqualTo(15L);
        assertThat(entity.getTieneCarrera()).isEqualTo((byte) 1);
        assertThat(entity.getIntercambio()).isEqualTo((byte) 1);
    }

}
