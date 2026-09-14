package um.tesoreria.core.hexagonal.personas.persona.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersonaNombresNormalizerTest {

    @Test
    void normalizaApellidoAMayusculas() {
        assertThat(PersonaNombresNormalizer.normalizarApellido("garcía")).isEqualTo("GARCÍA");
        assertThat(PersonaNombresNormalizer.normalizarApellido("García")).isEqualTo("GARCÍA");
        assertThat(PersonaNombresNormalizer.normalizarApellido("GARCÍA")).isEqualTo("GARCÍA");
    }

    @Test
    void normalizaApellidoCompuestoConservandoEspacios() {
        assertThat(PersonaNombresNormalizer.normalizarApellido("de la cruz")).isEqualTo("DE LA CRUZ");
        assertThat(PersonaNombresNormalizer.normalizarApellido("  perez    gomez  ")).isEqualTo("PEREZ GOMEZ");
    }

    @Test
    void normalizaApellidoConLetrasSuplementarias() {
        assertThat(PersonaNombresNormalizer.normalizarApellido("župan")).isEqualTo("ŽUPAN");
    }

    @Test
    void apellidoNullOBlancoSeDevuelveIgual() {
        assertThat(PersonaNombresNormalizer.normalizarApellido(null)).isNull();
        assertThat(PersonaNombresNormalizer.normalizarApellido("")).isEmpty();
        assertThat(PersonaNombresNormalizer.normalizarApellido("   ")).isEqualTo("   ");
    }

    @Test
    void normalizaNombreCapitalizadoPorPalabra() {
        assertThat(PersonaNombresNormalizer.normalizarNombre("juan pablo")).isEqualTo("Juan Pablo");
        assertThat(PersonaNombresNormalizer.normalizarNombre("JUAN PABLO")).isEqualTo("Juan Pablo");
        assertThat(PersonaNombresNormalizer.normalizarNombre("JuAn paBlo")).isEqualTo("Juan Pablo");
    }

    @Test
    void normalizaNombreConTildes() {
        assertThat(PersonaNombresNormalizer.normalizarNombre("ana sofía")).isEqualTo("Ana Sofía");
        assertThat(PersonaNombresNormalizer.normalizarNombre("MARÍA EUGENIA")).isEqualTo("María Eugenia");
        assertThat(PersonaNombresNormalizer.normalizarNombre("ñuñez")).isEqualTo("Ñuñez");
    }

    @Test
    void normalizaNombreCompuestoConGuion() {
        assertThat(PersonaNombresNormalizer.normalizarNombre("maría-eugenia")).isEqualTo("María-Eugenia");
    }

    @Test
    void normalizaNombreColapsandoEspacios() {
        assertThat(PersonaNombresNormalizer.normalizarNombre("  juan   carlos  ")).isEqualTo("Juan Carlos");
    }

    @Test
    void nombreNullOBlancoSeDevuelveIgual() {
        assertThat(PersonaNombresNormalizer.normalizarNombre(null)).isNull();
        assertThat(PersonaNombresNormalizer.normalizarNombre("")).isEmpty();
        assertThat(PersonaNombresNormalizer.normalizarNombre("   ")).isEqualTo("   ");
    }
}
