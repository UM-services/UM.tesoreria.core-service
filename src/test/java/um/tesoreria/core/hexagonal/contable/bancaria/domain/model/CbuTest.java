package um.tesoreria.core.hexagonal.contable.bancaria.domain.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class CbuTest {

    @ParameterizedTest
    @ValueSource(strings = {"", "0070999000001234567891", "0140999812345678901233"})
    void aceptaVacioYCbuConVerificadoresCorrectos(String cbu) {
        assertThat(Cbu.esValido(cbu)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "0070999100001234567891",   // verificador del bloque 1
            "0070999000001234567892",   // verificador del bloque 2
            "007099900000123456789",    // 21 dígitos
            "00709990000012345678911",  // 23 dígitos
            "0070999O00001234567891",   // letra
            "0070 99900000123456789",   // espacio
            "00709990-0000123456789",   // guion
    })
    void rechazaLoInvalido(String cbu) {
        assertThat(Cbu.esValido(cbu)).isFalse();
    }
}
