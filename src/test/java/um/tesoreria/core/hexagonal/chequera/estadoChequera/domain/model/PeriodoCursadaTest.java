package um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PeriodoCursadaTest {

    // protects=la traducción 0/1/2 de la facultad a ANUAL/PRIMER_SEMESTRE/SEGUNDO_SEMESTRE;
    // fails_when=un código válido cae en otro período;
    // no_claim=los códigos que devuelve hoy la facultad (se verificó aparte, solo 0, 1 y 2).
    @Test
    void translatesTheFacultyCodes() {
        assertThat(PeriodoCursada.desde(0)).isEqualTo(PeriodoCursada.ANUAL);
        assertThat(PeriodoCursada.desde(1)).isEqualTo(PeriodoCursada.PRIMER_SEMESTRE);
        assertThat(PeriodoCursada.desde(2)).isEqualTo(PeriodoCursada.SEGUNDO_SEMESTRE);
    }

    // protects=un código desconocido o ausente no rompe y queda en SIN_PERIODO;
    // fails_when=3 o null lanzan excepción o se asignan a un período real;
    // no_claim=que la facultad pueda mandar otros códigos.
    @Test
    void anyOtherCodeOrNullIsSinPeriodo() {
        assertThat(PeriodoCursada.desde(3)).isEqualTo(PeriodoCursada.SIN_PERIODO);
        assertThat(PeriodoCursada.desde(null)).isEqualTo(PeriodoCursada.SIN_PERIODO);
    }
}
