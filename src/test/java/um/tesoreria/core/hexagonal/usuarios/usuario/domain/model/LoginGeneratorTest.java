package um.tesoreria.core.hexagonal.usuarios.usuario.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginGeneratorTest {

    @Test
    void normalizaTildesEneMayusculasYCaracteresEspeciales() {
        assertThat(LoginGenerator.normalizar("María Paz Núñez")).isEqualTo("mariapaznunez");
        assertThat(LoginGenerator.normalizar("  O'Brien-De la Cruz  ")).isEqualTo("obriendelacruz");
        assertThat(LoginGenerator.normalizar("JOSÉ  ÁNGEL")).isEqualTo("joseangel");
        assertThat(LoginGenerator.normalizar(null)).isEmpty();
    }

    @Test
    void baseEsPrimeraLetraDelNombreMasApellido() {
        assertThat(LoginGenerator.candidatos("Juan", "Pérez").get(0)).isEqualTo("jperez");
        assertThat(LoginGenerator.candidatos("Gisella", "Núñez").get(0)).isEqualTo("gnunez");
        assertThat(LoginGenerator.candidatos("José Luis", "De la Cruz").get(0)).isEqualTo("jdelacruz");
    }

    @Test
    void agregaLetrasDelNombreAntesDeUsarNumeros() {
        List<String> candidatos = LoginGenerator.candidatos("Juan", "Pérez");

        assertThat(candidatos).startsWith("jperez", "juperez", "juaperez", "juanperez", "juanperez2");
    }

    @Test
    void nombreDeUnaSolaLetraPasaDirectoASufijoNumerico() {
        assertThat(LoginGenerator.candidatos("J", "Pérez"))
                .startsWith("jperez", "jperez2", "jperez3");
    }

    @Test
    void nombresCompuestosAvanzanSobreTodoElNombre() {
        List<String> candidatos = LoginGenerator.candidatos("María Paz", "Martínez");

        assertThat(candidatos).startsWith(
                "mmartinez", "mamartinez", "marmartinez", "marimartinez", "mariamartinez", "mariapmartinez");
    }

    @Test
    void nuncaSuperaElLargoMaximoNiRepiteCandidatos() {
        List<String> candidatos = LoginGenerator.candidatos("Maximiliano", "Fernández de la Torre");

        assertThat(candidatos).allSatisfy(c -> assertThat(c).hasSizeLessThanOrEqualTo(LoginGenerator.MAX_LENGTH));
        assertThat(candidatos).doesNotHaveDuplicates();
    }

    @Test
    void exigeNombreYApellidoUtilizables() {
        assertThatThrownBy(() -> LoginGenerator.candidatos("", "Pérez"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> LoginGenerator.candidatos("Juan", "!!! ###"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> LoginGenerator.candidatos("Juan", null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
