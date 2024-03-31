package um.tesoreria.core.service.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import um.tesoreria.core.kotlin.model.internal.ClickPagosEntity;
import um.tesoreria.core.service.ChequeraCuotaService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class ClickPagosService {

    @Autowired
    private ChequeraCuotaService chequeraCuotaService;

    public ClickPagosEntity processLine(String line, Boolean verify) {
        log.debug("line={}", line);
        ClickPagosEntity clickPagosEntity = null;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        if (line.startsWith("HEADER")) {
            LocalDate localDate = LocalDate.parse(line.substring(9, 17), dateTimeFormatter);
            OffsetDateTime fechaProceso = localDate.atStartOfDay().atOffset(ZoneOffset.UTC);
            localDate = LocalDate.parse(line.substring(17, 25), dateTimeFormatter);
            OffsetDateTime fechaEnvio = localDate.atStartOfDay().atOffset(ZoneOffset.UTC);
            clickPagosEntity = new ClickPagosEntity(line.substring(0, 6), Integer.parseInt(line.substring(6, 9)), fechaProceso, fechaEnvio, Integer.parseInt(line.substring(25, 30)), 0, BigDecimal.ZERO, 0, "", 0, "", 0, 0, 0, "", 0, 0, 0, "", BigDecimal.ZERO, BigDecimal.ZERO, 0, 0, 0, "", 0, 0, null, 0, 0, 0, null, 0, 0, "", null, "", 0, BigDecimal.ZERO, 0, 0, "", "", 0, "", "", "", 0, 0, 0, 0, 0, 0, "", "", null, false);
            try {
                log.debug("Header={}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(clickPagosEntity));
            } catch (JsonProcessingException e) {
                log.debug("Error Header");
            }
        } else if (line.startsWith("TRAILER")) {
            clickPagosEntity = new ClickPagosEntity(line.substring(0, 7), 0, null, null, 0, Integer.parseInt(line.substring(7, 15)), new BigDecimal(line.substring(15, 28)).divide(new BigDecimal(100)), Integer.parseInt(line.substring(28, 36)), "", 0, "", 0, 0, 0, "", 0, 0, 0, "", BigDecimal.ZERO, BigDecimal.ZERO, 0, 0, 0, "", 0, 0, null, 0, 0, 0, null, 0, 0, "", null, "", 0, BigDecimal.ZERO, 0, 0, "", "", 0, "", "", "", 0, 0, 0, 0, 0, 0, "", "", null, false);
            try {
                log.debug("Trailer={}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(clickPagosEntity));
            } catch (JsonProcessingException e) {
                log.debug("Error Trailer");
            }
        } else if (line.startsWith("DATOS")) {
            LocalDate localDate = LocalDate.parse(line.substring(145, 153), dateTimeFormatter);
            OffsetDateTime fechaLiquidacion = localDate.atStartOfDay().atOffset(ZoneOffset.UTC);
            localDate = LocalDate.parse("20" + line.substring(224, 230), dateTimeFormatter);
            OffsetDateTime fechaPago = localDate.atStartOfDay().atOffset(ZoneOffset.UTC);
            dateTimeFormatter = DateTimeFormatter.ofPattern("HHmmss");
            LocalTime localTime = LocalTime.parse(line.substring(129, 135), dateTimeFormatter);
            Integer facultadId = Integer.parseInt(line.substring(514, 516));
            Integer tipoChequeraId = Integer.parseInt(line.substring(516, 519));
            Long chequeraSerieId = Long.parseLong(line.substring(519, 524));
            Integer alternativaId = Integer.parseInt(line.substring(524, 526));
            Integer productoId = Integer.parseInt(line.substring(526, 527));
            Integer cuotaId = Integer.parseInt(line.substring(527, 529));
            clickPagosEntity = new ClickPagosEntity(line.substring(0, 5), Integer.parseInt(line.substring(8, 12)), null, null, 0, 0, new BigDecimal(line.substring(77, 88)).divide(new BigDecimal(100)), 0, line.substring(12, 13), Integer.parseInt(line.substring(13, 18)), line.substring(18, 28), Integer.parseInt(line.substring(28, 32)), Long.parseLong(line.substring(32, 40)), Long.parseLong(line.substring(40, 48)), line.substring(48, 50), Integer.parseInt(line.substring(50, 52)), Integer.parseInt(line.substring(52, 54)), Integer.parseInt(line.substring(54, 58)), line.substring(58, 77), new BigDecimal(line.substring(88, 99)), new BigDecimal(line.substring(99, 110)), Integer.parseInt(line.substring(110, 111)), Integer.parseInt(line.substring(111, 115)), Integer.parseInt(line.substring(115, 118)), line.substring(118, 120), Integer.parseInt(line.substring(120, 123)), Long.parseLong(line.substring(123, 129)), localTime, Integer.parseInt(line.substring(135, 138)), Integer.parseInt(line.substring(138, 141)), Integer.parseInt(line.substring(141, 145)), fechaLiquidacion, Long.parseLong(line.substring(153, 161)), Integer.parseInt(line.substring(161, 164)), line.substring(164, 224), fechaPago, line.substring(230, 231), Long.parseLong(line.substring(231, 238)), new BigDecimal(line.substring(238, 247)), Integer.parseInt(line.substring(247, 249)), Integer.parseInt(line.substring(249, 253)), line.substring(253, 256), line.substring(256, 271), Long.parseLong(line.substring(271, 279)), line.substring(279, 429), line.substring(429, 479), line.substring(479, 529), Integer.parseInt(line.substring(514, 516)), Integer.parseInt(line.substring(516, 519)), Long.parseLong(line.substring(519, 524)), Integer.parseInt(line.substring(524, 526)), Integer.parseInt(line.substring(526, 527)), Integer.parseInt(line.substring(527, 529)), line.substring(529, 579), line.substring(579, 629), chequeraCuotaService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId), false);
            try {
                log.debug("Datos={}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(clickPagosEntity));
            } catch (JsonProcessingException e) {
                log.debug("Error Datos");
            }
        }
        return clickPagosEntity;
    }

}
