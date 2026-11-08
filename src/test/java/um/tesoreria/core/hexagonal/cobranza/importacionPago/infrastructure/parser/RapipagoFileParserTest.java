package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.parser;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.LineaImportacion;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RapipagoFileParserTest {

    private final RapipagoFileParser parser = new RapipagoFileParser();

    @Test
    void armaUnaLineaConElPagoYElInstrumento() {
        String contenido = dato0(barra("01234", "7"), "20260315") + "\n"
                + dato1("000000012345", "20260317", "A") + "\n";

        List<LineaImportacion> lineas = parser.parsear("rapipago.txt",
                contenido.getBytes(StandardCharsets.ISO_8859_1));

        assertThat(lineas).hasSize(1);
        LineaImportacion linea = lineas.get(0);
        assertThat(linea.getNumeroLinea()).isEqualTo(1);
        assertThat(linea.getFacultadId()).isEqualTo(6);
        assertThat(linea.getTipoChequeraId()).isEqualTo(2);
        assertThat(linea.getChequeraSerieId()).isEqualTo(123L);
        assertThat(linea.getProductoId()).isEqualTo(1);
        assertThat(linea.getMes()).isEqualTo(3);
        assertThat(linea.getAnho()).isEqualTo(2026);
        assertThat(linea.getCuotaId()).isEqualTo(1);
        assertThat(linea.getVerificador()).isEqualTo("7");
        assertThat(linea.getImporte()).isEqualByComparingTo(new BigDecimal("123.45"));
        assertThat(linea.getFechaPago()).isEqualTo(OffsetDateTime.parse("2026-03-15T00:00:00Z"));
        assertThat(linea.getFechaAcreditacion()).isEqualTo(OffsetDateTime.parse("2026-03-17T00:00:00Z"));
        assertThat(linea.getMarcaAcreditacion()).isEqualTo("A");
        assertThat(linea.getTipoPagoId()).isEqualTo(3);
    }

    @Test
    void conservaLaMarcaPendienteParaQueElServicioDecida() {
        String contenido = dato0(barra("01234", "7"), "20260315") + "\n"
                + dato1("000000012345", "20260317", "P") + "\n";

        LineaImportacion linea = parser.parsear("rapipago.txt",
                contenido.getBytes(StandardCharsets.ISO_8859_1)).get(0);

        assertThat(linea.getMarcaAcreditacion()).isEqualTo("P");
        assertThat(linea.getFechaAcreditacion()).isEqualTo(OffsetDateTime.parse("2026-03-17T00:00:00Z"));
    }

    @Test
    void ignoraCabeceraTrailerLineasCortasYGruposSinInstrumento() {
        String contenido = "C" + " ".repeat(200) + "\n"
                + "D" + " ".repeat(20) + "\n"
                + "T" + " ".repeat(200) + "\n"
                + dato0(barra("01234", "7"), "20260315") + "\n"
                + "D" + " ".repeat(60) + "2" + "\n";

        assertThat(parser.parsear("rapipago.txt", contenido.getBytes(StandardCharsets.ISO_8859_1))).isEmpty();
    }

    @Test
    void numeraCadaGrupoConLaLineaDelRegistroCero() {
        String contenido = "C" + " ".repeat(200) + "\n"
                + dato0(barra("01234", "7"), "20260315") + "\n"
                + dato1("000000012345", "20260317", "A") + "\n"
                + dato0(barra("05678", "9"), "20260320") + "\n"
                + dato1("000000067890", "20260322", "P") + "\n";

        List<LineaImportacion> lineas = parser.parsear("rapipago.txt",
                contenido.getBytes(StandardCharsets.ISO_8859_1));

        assertThat(lineas).hasSize(2);
        assertThat(lineas.get(0).getNumeroLinea()).isEqualTo(2);
        assertThat(lineas.get(1).getNumeroLinea()).isEqualTo(4);
        assertThat(lineas.get(1).getImporte()).isEqualByComparingTo(new BigDecimal("678.90"));
    }

    private String dato0(String barra, String fechaPago) {
        char[] registro = registro(360);
        set(registro, 23, "D");
        set(registro, 43, "0");
        set(registro, 107, fechaPago);
        set(registro, 248, barra);
        return new String(registro);
    }

    private String dato1(String importe, String fechaAcreditacion, String marca) {
        char[] registro = registro(130);
        set(registro, 23, "D");
        set(registro, 43, "1");
        set(registro, 83, importe);
        set(registro, 98, fechaAcreditacion);
        set(registro, 106, marca);
        return new String(registro);
    }

    private String barra(String importe1, String verificador) {
        char[] barra = registro(100);
        set(barra, 1, "123");
        set(barra, 4, "06");
        set(barra, 6, "002");
        set(barra, 9, "00123");
        set(barra, 14, "1");
        set(barra, 15, "03");
        set(barra, 17, "26");
        set(barra, 19, "01");
        set(barra, 21, importe1);
        set(barra, 26, "15");
        set(barra, 28, "03");
        set(barra, 30, "2026");
        set(barra, 44, verificador);
        return new String(barra);
    }

    private char[] registro(int length) {
        char[] registro = new char[length];
        Arrays.fill(registro, ' ');
        return registro;
    }

    private void set(char[] registro, int posicion, String valor) {
        for (int i = 0; i < valor.length(); i++) {
            registro[posicion - 1 + i] = valor.charAt(i);
        }
    }

}
