package um.tesoreria.core.hexagonal.chequera.emision.domain.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CodigoI2of5Test {

    @Test
    void codificaParesConDelimitadores() {
        // "00" -> chr(35) = '#'
        assertEquals("!#\"", CodigoI2of5.codificar("00"));
    }

    @Test
    void usaTablaEspecialPara92a99() {
        String codificado = CodigoI2of5.codificar("9299");
        assertEquals((char) 33 + "" + (char) 196 + (char) 225 + (char) 34, codificado);
    }

    @Test
    void devuelveVacioSiLaCadenaEsImpar() {
        assertEquals("", CodigoI2of5.codificar("123"));
        assertEquals("", CodigoI2of5.codificar(""));
        assertEquals("", CodigoI2of5.codificar(null));
    }

    @Test
    void codificaUnCodigoRapiPagoCompleto() {
        String rapipago = "255" + "01" + "001" + "00001" + "1" + "03" + "26" + "01"
                + "0001000" + "01032026" + "00000" + "000" + "00000" + "000" + "0";
        String codificado = CodigoI2of5.codificar(rapipago);
        assertEquals(rapipago.length() / 2 + 2, codificado.length());
        assertEquals((char) 33, codificado.charAt(0));
        assertEquals((char) 34, codificado.charAt(codificado.length() - 1));
    }
}
