package um.tesoreria.core.hexagonal.contable.bancaria.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CuitTest {

    @ParameterizedTest
    @CsvSource({
            "30-51859446-6, 30-51859446-6",
            "30518594466, 30-51859446-6",
            "20-12345678-6, 20-12345678-6",
            "30-71234567-1, 30-71234567-1",
            "00-00000000-0, 00-00000000-0",
            "00000000000, 00-00000000-0",
            "27-20000000-0, 27-20000000-0",
    })
    void aceptaYGuardaConGuiones(String entrada, String esperado) {
        assertThat(Cuit.normalizar(entrada)).contains(esperado);
    }

    @Test
    void elVacioEsValido() {
        assertThat(Cuit.normalizar("")).contains("");
    }

    @Test
    void elSinDatoEsValido() {
        assertThat(Cuit.normalizar(Cuit.SIN_DATO)).contains(Cuit.SIN_DATO);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "30-51859446-5",     // verificador equivocado
            "30518594465",
            "3051859446",        // 10 dígitos
            "305185944666",      // 12 dígitos
            "30-5185944-66",     // guiones mal puestos
            "30 51859446 6",
            "AB-51859446-6",
            " 30-51859446-6",    // espacios: no se recortan
            "20-00000001-0",     // resto 1: el verificador sería 10 y no existe
            "20-00000001-9",
    })
    void rechazaLoInvalido(String entrada) {
        assertThat(Cuit.normalizar(entrada)).isEqualTo(Optional.empty());
    }
}
