/**
 *
 */
package um.tesoreria.rest.util;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import um.tesoreria.rest.util.transfer.FileInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 */
@Slf4j
public class Tool {

    public static OffsetDateTime hourAbsoluteArgentina() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTime(new Date());
        return calendar.getTime().toInstant().atOffset(ZoneOffset.UTC);
    }

    public static OffsetDateTime dateAbsoluteArgentina() {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime().toInstant().atOffset(ZoneOffset.UTC);
    }

    public static OffsetDateTime lastDayOfMonth(Integer anho, Integer mes) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(anho, mes - 1, 1, 0, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        return calendar.getTime().toInstant().atOffset(ZoneOffset.UTC);
    }

    public static OffsetDateTime firstTime(OffsetDateTime date) {
        return OffsetDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 0, 0, 0, 0,
                ZoneOffset.UTC);
    }

    public static OffsetDateTime lastTime(OffsetDateTime date) {
        return OffsetDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 23, 59, 59, 999,
                ZoneOffset.UTC);
    }

    public static File writeFile(FileInfo fileinfo) {
        // Reescribe archivo
        String filename = fileinfo.getFilename();
        filename = filename.replace('\\', '/');
        filename = filename.substring(filename.lastIndexOf('/') + 1);
        log.debug("Filename -> " + filename);
        File file = new File(filename);
        byte[] bytes = Base64.getDecoder().decode(fileinfo.getBase64());
        try {
            OutputStream output = new FileOutputStream(file);
            output.write(bytes);
            output.close();
        } catch (IOException e) {
            log.debug(e.getMessage());
        }
        return file;
    }

    private static String left(String string, Integer count) {
        if (string.length() > count) {
            return string.substring(0, count);
        }
        return string;
    }

    private static String right(String string, Integer count) {
        if (string.length() > count) {
            return string.substring(string.length() - count);
        }
        return string;
    }

    public static String number2Text(BigDecimal value) {
        value = value.setScale(2, RoundingMode.HALF_UP);
        String[] values = String.valueOf(value).split("\\.");
        String ultimo = number2TextEntero(new BigDecimal(values[0]));
        String centavos = " con " + values[1] + "/100";
        return (ultimo + centavos).trim();
    }

    public static String number2TextEntero(BigDecimal value) {
        String ultimo = "";
        long value_long = 0;
        long digitos = 0;
        switch (value.toString().length()) {
            case 1:
            case 2:
            case 3:
                ultimo = tresUltimas(value);
                break;
            case 4:
            case 5:
            case 6:
                ultimo = tresUltimas(value.divide(new BigDecimal(1000), 0, RoundingMode.DOWN));
                if (right(ultimo, 3).equals("uno")) {
                    ultimo = left(ultimo, ultimo.length() - 1);
                }
                ultimo = ultimo + " mil";
                value_long = value.longValue();
                digitos = value_long - (value_long / 1000) * 1000;
                if (digitos > 0) {
                    ultimo = ultimo + " " + tresUltimas(new BigDecimal(digitos)).trim();
                }
                break;
            case 7:
            case 8:
            case 9:
                ultimo = tresUltimas(value.divide(new BigDecimal(1000000), 0, RoundingMode.DOWN));
                if (right(ultimo, 3).equals("uno")) {
                    ultimo = left(ultimo, ultimo.length() - 1);
                }
                ultimo = ultimo + " millon";
                if (!ultimo.equals(" un millon")) {
                    ultimo = ultimo + "es";
                }
                value_long = value.longValue();
                digitos = value_long - (value_long / 1000000) * 1000000;
                if (digitos / 1000 > 0) {
                    ultimo = ultimo + " " + tresUltimas(new BigDecimal(digitos / 1000)).trim();
                    if (right(ultimo, 3).equals("uno")) {
                        ultimo = left(ultimo, ultimo.length() - 1);
                    }
                    ultimo = ultimo + " mil";
                }
                if (digitos - (digitos / 1000) * 1000 > 0) {
                    ultimo = ultimo + " " + tresUltimas(new BigDecimal(digitos - (digitos / 1000) * 1000)).trim();
                }
        }
        return ultimo.trim();
    }

    private static String tresUltimas(BigDecimal value) {
        String[] centenas = {"cien", "doscientos", "trescientos", "cuatrocientos", "quinientos", "seiscientos",
                "setecientos", "ochocientos", "novecientos"};
        String centena = "";
        String ultimo = "";

        int numero = value.intValue();

        if (numero > 99) {
            centena = centenas[(numero / 100) - 1];
        }
        if (numero > 100 && numero < 200) {
            centena = centena + "to";
        }
        if (numero != (numero / 100) * 100) {
            centena = centena + " ";
        }
        ultimo = dosUltimas(numero - (numero / 100) * 100);

        return centena + ultimo;
    }

    private static String dosUltimas(Integer value) {
        int largo = value.toString().trim().length();

        String unidades[] = {"uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve"};
        String decena[] = {"diez", "once", "doce", "trece", "catorce", "quince", "dieciseis", "diecisiete",
                "dieciocho", "diecinueve"};
        String decenas[] = {"veint", "treinta", "cuarenta", "cincuenta", "sesenta", "setenta", "ochenta", "noventa"};
        String ultimo = "";

        switch (largo) {
            case 1:
                if (value > 0) {
                    ultimo = unidades[value - 1];
                }
                break;
            case 2:
                if (value > 9 && value < 20) {
                    ultimo = decena[value - 10];
                }
                if (value > 19 && value < 100) {
                    ultimo = decenas[(value / 10) - 2];
                    if (value == 20) {
                        ultimo = ultimo + "e";
                    }
                    if (value > 20 && value < 30) {
                        ultimo = ultimo + "i";
                    }
                    if (value > 30 && value - (value / 10) * 10 > 0) {
                        ultimo = ultimo + " y ";
                    }
                    if (value - (value / 10) * 10 > 0) {
                        ultimo = ultimo + unidades[value - (value / 10) * 10 - 1];
                    }
                }
                break;
        }
        return ultimo;
    }

    public static String calculateVerificadorRapiPago(String codigoBarras) {
        String secuencia = "1";
        int repeticiones = 1 + codigoBarras.length() / 4;
        for (int ciclo = 1; ciclo <= repeticiones; ciclo++) {
            secuencia += "3579";
        }
        secuencia = Tool.left(secuencia, codigoBarras.length());
        log.debug("CodigoBarras -> {} / Secuencia -> {}", codigoBarras, secuencia);
        int acumulador = 0;
        for (int ciclo = 0; ciclo < codigoBarras.length(); ciclo++) {
            acumulador += Integer.parseInt(codigoBarras.substring(ciclo, ciclo + 1))
                    * Integer.parseInt(secuencia.substring(ciclo, ciclo + 1));
        }
        log.debug("Acumulador -> {}", acumulador);
        Integer resultado = acumulador / 2;
        log.debug("Resultado -> {}", resultado);
        return Tool.right(resultado.toString(), 1);
    }

    public static ResponseEntity<Resource> generateFile(String filename, String headerFilename) throws FileNotFoundException {
        File file = new File(filename);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + headerFilename);
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ResponseEntity.ok().headers(headers).contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
    }

}
