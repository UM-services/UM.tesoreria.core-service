package um.tesoreria.core.hexagonal.chequera.envio.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ChequeraEnvioAltaTest {

    private static ChequeraEnvioAlta alta(String emailPersonal, boolean personalValido,
                                          String emailInstitucional, boolean institucionalValido) {
        return new ChequeraEnvioAlta(342038L, 2, 520, 446L, 1, new BigDecimal("1001"), 1, 37, 1,
                "ABALLAY MURUA", "Luna Martina", "Matrícula y Arancel", "Ciclo Completo",
                emailPersonal, emailInstitucional, personalValido, institucionalValido, true, false);
    }

    @Test
    void numeroTieneElFormatoDelLegacy() {
        assertThat(alta("a@b.com", true, null, false).numero()).isEqualTo("2/520/446");
    }

    @Test
    void apellidoNombreTieneElFormatoDelLegacy() {
        assertThat(alta(null, false, null, false).apellidoNombre()).isEqualTo("ABALLAY MURUA, Luna Martina");
    }

    @Test
    void apellidoNombreSinDatosNoRompe() {
        ChequeraEnvioAlta sinPersona = new ChequeraEnvioAlta(1L, 2, 520, 446L, 1, new BigDecimal("1"), 1, 37, 1,
                null, null, null, null, null, null, false, false, false, false);
        assertThat(sinPersona.apellidoNombre()).isEmpty();
    }

    @Test
    void hayCorreoValidoSiAlgunoDeLosDosEsValido() {
        assertThat(alta("a@b.com", true, null, false).hayCorreoValido()).isTrue();
        assertThat(alta(null, false, "a@um.edu.ar", true).hayCorreoValido()).isTrue();
        assertThat(alta("mal", false, "", false).hayCorreoValido()).isFalse();
    }
}
