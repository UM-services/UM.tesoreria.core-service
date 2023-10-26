/**
 *
 */
package um.tesoreria.rest.service.facade;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.kotlin.model.view.ChequeraCuotaDeuda;
import um.tesoreria.rest.service.view.ChequeraCuotaDeudaService;
import um.tesoreria.rest.util.Tool;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class PagarFileService {

    @Autowired
    private ChequeraCuotaDeudaService chequeraCuotaDeudaService;

    @Autowired
    private Environment env;

    public String generateFiles(OffsetDateTime desde, OffsetDateTime hasta) throws IOException {
        String path = env.getProperty("path.files");
        String outputFilename = path + "PAGAR.zip";

        List<Character> meses = List.of('1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C');
        Integer cantidadLote = 5000;
        Integer totalRegistros = chequeraCuotaDeudaService.findAllByRango(desde, hasta, false, null).size();
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(outputFilename));

        for (Integer lote = 0; lote < (totalRegistros / cantidadLote) + 1; lote++) {
            String filename = path + "PFXG" + lote + meses.get(desde.getMonthValue() - 1)
                    + new DecimalFormat("00").format(OffsetDateTime.now().getDayOfMonth());
            String filenameControl = path + "CFXG" + lote + meses.get(desde.getMonthValue() - 1)
                    + new DecimalFormat("00").format(OffsetDateTime.now().getDayOfMonth());
            log.debug("Filename -> {} Control -> {}", filename, filenameControl);
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            // Registro inicial REFRESH
            String line = "HRFACTURACION";
            line += "FXG";
            OffsetDateTime fecha = Tool.dateAbsoluteArgentina();
            line += fecha.format(DateTimeFormatter.ofPattern("yyMMdd"));
            line += new DecimalFormat("00000").format(lote);
            line += String.format("%1$" + 104 + "s", "");
            out.write(line);
            out.write("\r\n");
            // Identificador de deuda
            Integer cantidadRegistros = 0;
            BigDecimal totalVencimiento1 = BigDecimal.ZERO;
            BigDecimal totalVencimiento2 = BigDecimal.ZERO;
            BigDecimal totalVencimiento3 = BigDecimal.ZERO;
            OffsetDateTime lastDate = OffsetDateTime.of(1960, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
            for (ChequeraCuotaDeuda deuda : chequeraCuotaDeudaService.findAllByRango(desde, hasta, true, PageRequest.of(lote, cantidadLote))) {
                cantidadRegistros++;
                // Identificador de Deuda
                line = new DecimalFormat("00").format(deuda.getAlternativaId());
                line += deuda.getProductoId();
                line += new DecimalFormat("00").format(deuda.getCuotaId());
                // Identificador de concepto
                line += "001";
                // Identificador de usuario
                line += new DecimalFormat("00").format(deuda.getFacultadId());
                line += new DecimalFormat("000").format(deuda.getTipoChequeraId());
                line += new DecimalFormat("00000").format(deuda.getChequeraSerieId());
                line += String.format("%1$" + 9 + "s", "");
                // Fecha 1er vencimiento
                line += deuda.getVencimiento1().plusHours(3).format(DateTimeFormatter.ofPattern("yyMMdd"));
                // Importe 1er vencimiento
                line += new DecimalFormat("000000000000").format(deuda.getImporte1().multiply(new BigDecimal(100)));
                // Fecha 2do vencimiento
                line += deuda.getVencimiento2().plusHours(3).format(DateTimeFormatter.ofPattern("yyMMdd"));
                // Importe 2do vencimiento
                line += new DecimalFormat("000000000000").format(deuda.getImporte2().multiply(new BigDecimal(100)));
                // Fecha 3er vencimiento
                line += deuda.getVencimiento3().plusHours(3).format(DateTimeFormatter.ofPattern("yyMMdd"));
                // Importe 3er vencimiento
                line += new DecimalFormat("000000000000").format(deuda.getImporte3().multiply(new BigDecimal(100)));
                // Discrecional
                line += String.format("%1$" + 50 + "s", "");
                out.write(line);
                out.write("\r\n");
                totalVencimiento1 = totalVencimiento1.add(deuda.getImporte1());
                totalVencimiento2 = totalVencimiento2.add(deuda.getImporte2());
                totalVencimiento3 = totalVencimiento3.add(deuda.getImporte3());
                if (lastDate == null || deuda.getVencimiento3().plusHours(3).isAfter(lastDate)) {
                    lastDate = deuda.getVencimiento3().plusHours(3);
                }
            }
            // Registro final
            line = "TRFACTURACION";
            line += new DecimalFormat("00000000").format(cantidadRegistros + 2);
            line += new DecimalFormat("000000000000000000").format(totalVencimiento1.multiply(new BigDecimal(100)));
            line += new DecimalFormat("000000000000000000").format(totalVencimiento2.multiply(new BigDecimal(100)));
            line += new DecimalFormat("000000000000000000").format(totalVencimiento3.multiply(new BigDecimal(100)));
            line += String.format("%1$" + 56 + "s", "");
            out.write(line);
            out.write("\r\n");
            out.close();
            BufferedWriter outControl = new BufferedWriter(new FileWriter(filenameControl));
            // Registro inicial CONTROL
            line = "HRPASCTRL";
            line += fecha.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            line += "FXG";
            // Debo extraer sólo el nombre del archivo
            File file = new File(filename);
            line += file.getName();
            line += new DecimalFormat("0000000000").format((cantidadRegistros + 2) * 133L);
            line += String.format("%1$" + 37 + "s", "");
            outControl.write(line);
            outControl.write("\r\n");
            // Registro datos CONTROL
            line = "LOTES";
            line += new DecimalFormat("00000").format(lote);
            line += new DecimalFormat("00000000").format(cantidadRegistros + 2);
            line += new DecimalFormat("000000000000000000").format(totalVencimiento1.multiply(new BigDecimal(100)));
            line += new DecimalFormat("000000000000000000").format(totalVencimiento2.multiply(new BigDecimal(100)));
            line += new DecimalFormat("000000000000000000").format(totalVencimiento3.multiply(new BigDecimal(100)));
            line += String.format("%1$" + 3 + "s", "");
            outControl.write(line);
            outControl.write("\r\n");
            // Registro final CONTROL
            line = "FINAL";
            line += new DecimalFormat("00000000").format(cantidadRegistros + 2);
            line += new DecimalFormat("000000000000000000").format(totalVencimiento1.multiply(new BigDecimal(100)));
            line += new DecimalFormat("000000000000000000").format(totalVencimiento2.multiply(new BigDecimal(100)));
            line += new DecimalFormat("000000000000000000").format(totalVencimiento3.multiply(new BigDecimal(100)));
            line += lastDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            outControl.write(line);
            outControl.write("\r\n");
            outControl.close();

            byte[] buffer = new byte[1024];
            log.debug("Deflating {} ...", filename);
            zipOutputStream.putNextEntry(new ZipEntry(new File(filename).getName()));
            FileInputStream fileInputStream = new FileInputStream(filename);
            int len;
            while ((len = fileInputStream.read(buffer)) > 0) {
                zipOutputStream.write(buffer, 0, len);
            }
            fileInputStream.close();
            zipOutputStream.closeEntry();

            log.debug("Deflating {} ...", filenameControl);
            zipOutputStream.putNextEntry(new ZipEntry(new File(filenameControl).getName()));
            fileInputStream = new FileInputStream(filenameControl);
            while ((len = fileInputStream.read(buffer)) > 0) {
                zipOutputStream.write(buffer, 0, len);
            }
            fileInputStream.close();
            zipOutputStream.closeEntry();
        }
        zipOutputStream.close();
        return outputFilename;
    }

}
