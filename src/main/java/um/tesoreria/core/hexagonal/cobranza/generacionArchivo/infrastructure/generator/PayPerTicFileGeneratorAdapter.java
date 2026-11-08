package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.ResultadoGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.out.PayPerTicFileGenerator;
import um.tesoreria.core.service.facade.PayPerTicFileService;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class PayPerTicFileGeneratorAdapter implements PayPerTicFileGenerator {

    private static final String CONTENT_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final PayPerTicFileService payPerTicFileService;

    @Override
    public ResultadoGeneracion generar(OffsetDateTime desde, OffsetDateTime hasta) {
        try {
            Path path = Path.of(payPerTicFileService.generate(desde, hasta));
            return new ResultadoGeneracion(path.getFileName().toString(), CONTENT_TYPE,
                    Files.readAllBytes(path), 0, BigDecimal.ZERO);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo generar el archivo PayPerTIC", e);
        }
    }

}
